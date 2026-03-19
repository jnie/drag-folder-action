package dk.jnie.dragfolder.domain.model;

import java.nio.file.Path;
import java.util.Objects;

public final class FileEvent {
    private final Path filePath;
    private final String fileName;
    private final FileType fileType;

    private FileEvent(Path filePath, String fileName, FileType fileType) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public static FileEvent create(Path filePath, String fileName, FileType fileType) {
        return new FileEvent(filePath, fileName, fileType);
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEvent fileEvent = (FileEvent) o;
        return Objects.equals(filePath, fileEvent.filePath) &&
                Objects.equals(fileName, fileEvent.fileName) &&
                fileType == fileEvent.fileType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, fileName, fileType);
    }

    @Override
    public String toString() {
        return "FileEvent{" +
                "filePath=" + filePath +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                '}';
    }
}