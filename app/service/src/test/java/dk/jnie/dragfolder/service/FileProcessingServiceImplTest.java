package dk.jnie.dragfolder.service;

import dk.jnie.dragfolder.domain.model.Configuration;
import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import dk.jnie.dragfolder.domain.outbound.FileHandler;
import dk.jnie.dragfolder.domain.outbound.FileSystemMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileProcessingServiceImpl Tests")
class FileProcessingServiceImplTest {

    @Mock
    private FileSystemMonitor fileSystemMonitor;

    @Mock
    private FileHandler zipHandler;

    private FileProcessingServiceImpl fileProcessingService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Setup the mock handler to indicate it can handle ZIP files
        lenient().when(zipHandler.canHandle(any(FileEvent.class))).thenAnswer(invocation -> {
            FileEvent event = invocation.getArgument(0);
            return event.getFileType() == FileType.ZIP;
        });
        
        fileProcessingService = new FileProcessingServiceImpl(fileSystemMonitor, Collections.singletonList(zipHandler));
    }

    @Test
    @DisplayName("Should start monitoring when folder exists")
    void shouldStartMonitoringWhenFolderExists() {
        // Given
        Configuration config = Configuration.create(
                tempDir.toString(),
                tempDir.resolve("output").toString(),
                5,
                false
        );

        // When
        fileProcessingService.startMonitoring(config);

        // Then
        verify(fileSystemMonitor).startMonitoring(eq(tempDir), any(Consumer.class), eq(5));
    }

    @Test
    @DisplayName("Should not start monitoring when folder does not exist")
    void shouldNotStartMonitoringWhenFolderDoesNotExist() {
        // Given
        Configuration config = Configuration.create(
                "/nonexistent/folder",
                "/tmp/output",
                5,
                false
        );

        // When
        fileProcessingService.startMonitoring(config);

        // Then
        verify(fileSystemMonitor, never()).startMonitoring(any(), any());
    }

    @Test
    @DisplayName("Should stop monitoring when requested")
    void shouldStopMonitoringWhenRequested() {
        // Given
        Configuration config = Configuration.create(
                tempDir.toString(),
                tempDir.resolve("output").toString(),
                5,
                false
        );
        fileProcessingService.startMonitoring(config);

        // When
        fileProcessingService.stopMonitoring();

        // Then
        verify(fileSystemMonitor).stopMonitoring();
    }

    @Test
    @DisplayName("Should delegate file check to file system monitor")
    void shouldDelegateFileCheckToFileSystemMonitor() {
        // Given
        List<FileEvent> expectedFiles = Arrays.asList(
                FileEvent.create(tempDir.resolve("file1.txt"), "file1.txt", FileType.TXT),
                FileEvent.create(tempDir.resolve("file2.zip"), "file2.zip", FileType.ZIP)
        );
        when(fileSystemMonitor.getNewFiles(tempDir)).thenReturn(expectedFiles);

        // When
        List<FileEvent> result = fileProcessingService.checkForNewFiles(tempDir);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedFiles);
        verify(fileSystemMonitor).getNewFiles(tempDir);
    }

    @Test
    @DisplayName("Should process file with matching handler")
    void shouldProcessFileWithMatchingHandler() {
        // Given
        FileEvent zipEvent = FileEvent.create(
                tempDir.resolve("test.zip"),
                "test.zip",
                FileType.ZIP
        );

        // When
        fileProcessingService.processFile(zipEvent);

        // Then
        verify(zipHandler).handle(eq(zipEvent), any(Path.class));
    }

    @Test
    @DisplayName("Should not process file when no handler matches")
    void shouldNotProcessFileWhenNoHandlerMatches() {
        // Given
        FileEvent txtEvent = FileEvent.create(
                tempDir.resolve("test.txt"),
                "test.txt",
                FileType.TXT
        );

        // When
        fileProcessingService.processFile(txtEvent);

        // Then
        verify(zipHandler, never()).handle(any(), any());
    }

    @Test
    @DisplayName("Should return empty list when folder has no files")
    void shouldReturnEmptyListWhenFolderHasNoFiles() {
        // Given
        when(fileSystemMonitor.getNewFiles(tempDir)).thenReturn(Collections.emptyList());

        // When
        List<FileEvent> result = fileProcessingService.checkForNewFiles(tempDir);

        // Then
        assertThat(result).isEmpty();
    }
}
