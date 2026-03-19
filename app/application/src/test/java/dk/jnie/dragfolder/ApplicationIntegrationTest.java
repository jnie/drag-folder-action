package dk.jnie.dragfolder;

import dk.jnie.dragfolder.domain.model.Configuration;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("Should load Spring context successfully")
    void shouldLoadSpringContextSuccessfully() {
        // Given - Spring context is loaded
        // Then - all beans are wired correctly
        assertThat(fileProcessingService).isNotNull();
    }
}
