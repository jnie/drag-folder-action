package dk.jnie.dragfolder.domain.services;

import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.Configuration;
import java.nio.file.Path;
import java.util.List;

public interface FileProcessingService {
    void startMonitoring(Configuration config);
    void stopMonitoring();
    void processFile(FileEvent fileEvent);
    List<FileEvent> checkForNewFiles(Path folderPath);
}