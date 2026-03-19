package dk.jnie.dragfolder.outbound.filesystem;

import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import dk.jnie.dragfolder.domain.outbound.FileSystemMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class WatchServiceFileSystemMonitor implements FileSystemMonitor {
    private static final Logger log = LoggerFactory.getLogger(WatchServiceFileSystemMonitor.class);

    private final Map<String, Boolean> existingFiles = new ConcurrentHashMap<>();
    private volatile boolean monitoring = false;
    private Thread monitorThread;
    private Consumer<FileEvent> onNewFileCallback;

    @Override
    public void startMonitoring(Path folderPath, Consumer<FileEvent> onNewFile) {
        this.onNewFileCallback = onNewFile;
        monitoring = true;

        monitorThread = new Thread(() -> {
            while (monitoring) {
                try {
                    Thread.sleep(5000);
                    List<FileEvent> newFiles = getNewFiles(folderPath);
                    for (FileEvent event : newFiles) {
                        if (onNewFileCallback != null) {
                            onNewFileCallback.accept(event);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        monitorThread.start();
    }

    @Override
    public void stopMonitoring() {
        monitoring = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
    }

    @Override
    public List<FileEvent> getNewFiles(Path folderPath) {
        List<FileEvent> newFiles = new ArrayList<>();
        File folder = folderPath.toFile();

        if (!folder.exists() || !folder.isDirectory()) {
            return newFiles;
        }

        File[] fileList = folder.listFiles();
        if (fileList == null) {
            return newFiles;
        }

        for (File file : fileList) {
            if (file.isDirectory()) {
                continue;
            }

            String fileName = file.getName();
            if (!existingFiles.containsKey(fileName)) {
                existingFiles.put(fileName, true);
                FileType fileType = determineFileType(fileName);
                FileEvent event = FileEvent.create(
                        file.toPath(),
                        fileName,
                        fileType);
                newFiles.add(event);
                log.info("New file detected: {}", fileName);
            }
        }

        return newFiles;
    }

    private FileType determineFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return FileType.UNKNOWN;
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        try {
            return FileType.valueOf(extension);
        } catch (IllegalArgumentException e) {
            return FileType.UNKNOWN;
        }
    }
}