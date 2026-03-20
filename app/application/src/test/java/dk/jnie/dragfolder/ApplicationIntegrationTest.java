package dk.jnie.dragfolder;

import dk.jnie.dragfolder.domain.model.Configuration;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.main.web-application-type=none",
    "timer.seconds=1",
    "monitor.folder=/tmp/monitor",
    "output.folder=/tmp/output",
    "cli.enabled=false"
})
@DisplayName("Application Integration Tests")
class ApplicationIntegrationTest {

    @Autowired
    private FileProcessingService fileProcessingService;

    @TempDir
    Path tempDir;

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should autowire file processing service")
    void shouldAutowireFileProcessingService() {
        assertThat(fileProcessingService).isNotNull();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Should load Spring context successfully")
    void shouldLoadSpringContextSuccessfully() {
        // Given - Spring context is loaded
        // Then - all beans are wired correctly
        assertThat(fileProcessingService).isNotNull();
    }
}
