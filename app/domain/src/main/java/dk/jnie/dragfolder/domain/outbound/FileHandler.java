package dk.jnie.dragfolder.domain.outbound;

import dk.jnie.dragfolder.domain.model.FileEvent;
import java.nio.file.Path;

public interface FileHandler {
    boolean canHandle(FileEvent fileEvent);
    void handle(FileEvent fileEvent, Path outputFolder);
}