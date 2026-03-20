package dk.jnie.dragfolder.domain.outbound;

import dk.jnie.dragfolder.domain.model.FileEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface FileSystemMonitor {
    void startMonitoring(Path folderPath, Consumer<FileEvent> onNewFile);
    void startMonitoring(Path folderPath, Consumer<FileEvent> onNewFile, Integer timeoutSeconds);
    void stopMonitoring();
    List<FileEvent> getNewFiles(Path folderPath);
}