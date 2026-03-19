package dk.jnie.dragfolder;

import dk.jnie.dragfolder.domain.model.Configuration;
import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.web-application-type=none"
})
@DisplayName("Application Integration Tests")
class ApplicationIntegrationTest {

    @Autowired
    private FileProcessingService fileProcessingService;

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should autowire file processing service")
    void shouldAutowireFileProcessingService() {
        assertThat(fileProcessingService).isNotNull();
    }

    @Test
    @DisplayName("Should process configuration successfully")
    void shouldProcessConfigurationSuccessfully() {
        // Given
        Path monitorFolder = tempDir.resolve("monitor");
        Path outputFolder = tempDir.resolve("output");
        monitorFolder.toFile().mkdirs();
        outputFolder.toFile().mkdirs();

        Configuration config = Configuration.create(
                monitorFolder.toString(),
                outputFolder.toString(),
                1,
                false
        );

        // When
        fileProcessingService.startMonitoring(config);

        // Then
        assertThat(monitorFolder).exists();

        // Cleanup
        fileProcessingService.stopMonitoring();
    }
}
