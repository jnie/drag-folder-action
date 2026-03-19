package dk.jnie.dragfolder.domain.model;

import dk.jnie.dragfolder.domain.util.ObjectStyle;
import java.nio.file.Path;

@ObjectStyle
@Value.Immutable
public interface FileEventDef {
    Path getFilePath();
    String getFileName();
    FileType getFileType();
}