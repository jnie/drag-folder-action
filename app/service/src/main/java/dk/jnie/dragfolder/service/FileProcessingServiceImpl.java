package dk.jnie.dragfolder.service;

import dk.jnie.dragfolder.domain.model.Configuration;
import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import dk.jnie.dragfolder.domain.outbound.FileHandler;
import dk.jnie.dragfolder.domain.outbound.FileSystemMonitor;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jnie.dragfolder.domain.model.FileEvent.FileEventBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileProcessingServiceImpl implements FileProcessingService {
    private static final Logger log = LoggerFactory.getLogger(FileProcessingServiceImpl.class);

    private final FileSystemMonitor fileSystemMonitor;
    private final Map<FileType, FileHandler> handlers;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public FileProcessingServiceImpl(FileSystemMonitor fileSystemMonitor, List<FileHandler> handlers) {
        this.fileSystemMonitor = fileSystemMonitor;
        Map<FileType, FileHandler> handlerMap = new ConcurrentHashMap<>();
        for (FileHandler handler : handlers) {
            for (FileType type : FileType.values()) {
                if (type != FileType.UNKNOWN) {
                    FileEvent testEvent = FileEvent.builder()
                            .filePath(Path.of("test." + type.name().toLowerCase()))
                            .fileName("test." + type.name().toLowerCase())
                            .fileType(type)
                            .build();
                    if (handler.canHandle(testEvent)) {
                        handlerMap.put(type, handler);
                    }
                }
            }
        }
        this.handlers = handlerMap;
    }

    @Override
    public void startMonitoring(Configuration config) {
        if (running.getAndSet(true)) {
            log.warn("Already monitoring");
            return;
        }

        Path monitorPath = Path.of(config.getMonitorFolder());
        if (!Files.isDirectory(monitorPath)) {
            log.error("Monitor folder does not exist: {}", monitorPath);
            running.set(false);
            return;
        }

        log.info("Starting to monitor folder: {}", monitorPath);
        fileSystemMonitor.startMonitoring(monitorPath, this::processFile);
    }

    @Override
    public void stopMonitoring() {
        log.info("Stopping monitoring");
        running.set(false);
        fileSystemMonitor.stopMonitoring();
    }

    @Override
    public void processFile(FileEvent fileEvent) {
        log.info("Processing file: {}", fileEvent.getFileName());

        Optional<FileHandler> handler = handlers.entrySet().stream()
                .filter(e -> e.getKey() == fileEvent.getFileType())
                .map(Map.Entry::getValue)
                .findFirst();

        if (handler.isPresent()) {
            Path outputFolder = Path.of(System.getProperty("java.io.tmpdir"));
            handler.get().handle(fileEvent, outputFolder);
        } else {
            log.warn("No handler found for file type: {}", fileEvent.getFileType());
        }
    }

    @Override
    public List<FileEvent> checkForNewFiles(Path folderPath) {
        return fileSystemMonitor.getNewFiles(folderPath);
    }
}