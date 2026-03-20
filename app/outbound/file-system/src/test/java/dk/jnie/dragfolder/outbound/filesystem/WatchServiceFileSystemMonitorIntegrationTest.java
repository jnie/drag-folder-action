package dk.jnie.dragfolder.outbound.filesystem;

import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@DisplayName("WatchServiceFileSystemMonitor Integration Tests")
class WatchServiceFileSystemMonitorIntegrationTest {

    private WatchServiceFileSystemMonitor monitor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        monitor = new WatchServiceFileSystemMonitor();
    }

    @AfterEach
    void tearDown() {
        monitor.stopMonitoring();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should detect new files in folder")
    void shouldDetectNewFilesInFolder() throws IOException {
        // Given
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);

        // When
        List<FileEvent> newFiles = monitor.getNewFiles(tempDir);

        // Then
        assertThat(newFiles).hasSize(1);
        assertThat(newFiles.get(0).getFileName()).isEqualTo("test.txt");
        assertThat(newFiles.get(0).getFileType()).isEqualTo(FileType.TXT);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should return empty list for empty folder")
    void shouldReturnEmptyListForEmptyFolder() {
        // When
        List<FileEvent> newFiles = monitor.getNewFiles(tempDir);

        // Then
        assertThat(newFiles).isEmpty();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should return empty list for non-existent folder")
    void shouldReturnEmptyListForNonExistentFolder() {
        // Given
        Path nonExistent = Path.of("/this/does/not/exist");

        // When
        List<FileEvent> newFiles = monitor.getNewFiles(nonExistent);

        // Then
        assertThat(newFiles).isEmpty();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should correctly identify file types by extension")
    void shouldCorrectlyIdentifyFileTypesByExtension() throws IOException {
        // Given
        Files.createFile(tempDir.resolve("doc.zip"));
        Files.createFile(tempDir.resolve("readme.txt"));
        Files.createFile(tempDir.resolve("data.xml"));
        Files.createFile(tempDir.resolve("song.mp3"));
        Files.createFile(tempDir.resolve("backup.wrk"));

        // When
        List<FileEvent> newFiles = monitor.getNewFiles(tempDir);

        // Then
        assertThat(newFiles).hasSize(5);
        assertThat(newFiles.stream().anyMatch(e -> e.getFileName().equals("doc.zip") && e.getFileType() == FileType.ZIP)).isTrue();
        assertThat(newFiles.stream().anyMatch(e -> e.getFileName().equals("readme.txt") && e.getFileType() == FileType.TXT)).isTrue();
        assertThat(newFiles.stream().anyMatch(e -> e.getFileName().equals("data.xml") && e.getFileType() == FileType.XML)).isTrue();
        assertThat(newFiles.stream().anyMatch(e -> e.getFileName().equals("song.mp3") && e.getFileType() == FileType.MP3)).isTrue();
        assertThat(newFiles.stream().anyMatch(e -> e.getFileName().equals("backup.wrk") && e.getFileType() == FileType.WRK)).isTrue();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should mark unknown file types correctly")
    void shouldMarkUnknownFileTypesCorrectly() throws IOException {
        // Given
        Files.createFile(tempDir.resolve("unknown.bin"));

        // When
        List<FileEvent> newFiles = monitor.getNewFiles(tempDir);

        // Then
        assertThat(newFiles).hasSize(1);
        assertThat(newFiles.get(0).getFileType()).isEqualTo(FileType.UNKNOWN);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should skip directories when scanning for files")
    void shouldSkipDirectoriesWhenScanningForFiles() throws IOException {
        // Given
        Files.createDirectory(tempDir.resolve("subdir"));
        Files.createFile(tempDir.resolve("file.txt"));

        // When
        List<FileEvent> newFiles = monitor.getNewFiles(tempDir);

        // Then
        assertThat(newFiles).hasSize(1);
        assertThat(newFiles.get(0).getFileName()).isEqualTo("file.txt");
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should not return same file twice")
    void shouldNotReturnSameFileTwice() throws IOException {
        // Given
        Files.createFile(tempDir.resolve("test.txt"));

        // When
        List<FileEvent> firstScan = monitor.getNewFiles(tempDir);
        List<FileEvent> secondScan = monitor.getNewFiles(tempDir);

        // Then
        assertThat(firstScan).hasSize(1);
        assertThat(secondScan).isEmpty();
    }

}
