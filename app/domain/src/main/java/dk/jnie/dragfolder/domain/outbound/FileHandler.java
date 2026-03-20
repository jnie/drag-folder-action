package dk.jnie.dragfolder.domain.outbound;

import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import java.nio.file.Path;
import java.util.Set;

public interface FileHandler {
    boolean canHandle(FileEvent fileEvent);
    void handle(FileEvent fileEvent, Path outputFolder);
    Set<FileType> getSupportedTypes();
}