package dk.jnie.dragfolder.outbound.zip;

import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ZipFileHandler Integration Tests")
class ZipFileHandlerIntegrationTest {

    private final ZipFileHandler zipFileHandler = new ZipFileHandler();

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should handle ZIP file type")
    void shouldHandleZipFileType() {
        // Given
        FileEvent zipEvent = FileEvent.create(
                tempDir.resolve("test.zip"),
                "test.zip",
                FileType.ZIP
        );

        // Then
        assertThat(zipFileHandler.canHandle(zipEvent)).isTrue();
    }

    @Test
    @DisplayName("Should not handle non-ZIP file types")
    void shouldNotHandleNonZipFileTypes() {
        // Given
        FileEvent txtEvent = FileEvent.create(
                tempDir.resolve("test.txt"),
                "test.txt",
                FileType.TXT
        );

        // Then
        assertThat(zipFileHandler.canHandle(txtEvent)).isFalse();
    }

    @Test
    @DisplayName("Should extract ZIP file contents")
    void shouldExtractZipFileContents() throws IOException {
        // Given
        Path zipFile = tempDir.resolve("test.zip");
        Path outputDir = tempDir.resolve("output");
        
        // Create a ZIP file with content
        try (FileOutputStream fos = new FileOutputStream(zipFile.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            ZipEntry entry = new ZipEntry("testfile.txt");
            zos.putNextEntry(entry);
            zos.write("Hello World".getBytes());
            zos.closeEntry();
        }

        FileEvent zipEvent = FileEvent.create(zipFile, "test.zip", FileType.ZIP);

        // When
        zipFileHandler.handle(zipEvent, outputDir);

        // Then
        Path extractedFile = outputDir.resolve("test").resolve("testfile.txt");
        assertThat(extractedFile).exists();
        assertThat(Files.readString(extractedFile)).isEqualTo("Hello World");
    }

    @Test
    @DisplayName("Should create output directory when it does not exist")
    void shouldCreateOutputDirectoryWhenItDoesNotExist() throws IOException {
        // Given
        Path zipFile = tempDir.resolve("archive.zip");
        Path outputDir = tempDir.resolve("nonexistent").resolve("nested");
        
        try (FileOutputStream fos = new FileOutputStream(zipFile.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            ZipEntry entry = new ZipEntry("data.txt");
            zos.putNextEntry(entry);
            zos.write("Test data".getBytes());
            zos.closeEntry();
        }

        FileEvent zipEvent = FileEvent.create(zipFile, "archive.zip", FileType.ZIP);

        // When
        zipFileHandler.handle(zipEvent, outputDir);

        // Then
        assertThat(outputDir.resolve("archive")).exists();
        assertThat(outputDir.resolve("archive").resolve("data.txt")).exists();
    }
}
