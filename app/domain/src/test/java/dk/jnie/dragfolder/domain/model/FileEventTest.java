package dk.jnie.dragfolder.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FileEvent Tests")
class FileEventTest {

    @Test
    @DisplayName("Should create file event with all properties")
    void shouldCreateFileEventWithAllProperties() {
        // Given
        Path filePath = Paths.get("/tmp/test.zip");
        String fileName = "test.zip";
        FileType fileType = FileType.ZIP;

        // When
        FileEvent event = FileEvent.create(filePath, fileName, fileType);

        // Then
        assertThat(event.getFilePath()).isEqualTo(filePath);
        assertThat(event.getFileName()).isEqualTo(fileName);
        assertThat(event.getFileType()).isEqualTo(fileType);
    }

    @Test
    @DisplayName("Should support all file types")
    void shouldSupportAllFileTypes() {
        // When & Then
        for (FileType type : FileType.values()) {
            Path filePath = Paths.get("/tmp/test." + type.name().toLowerCase());
            FileEvent event = FileEvent.create(filePath, "test." + type.name().toLowerCase(), type);
            assertThat(event.getFileType()).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("Should consider two events equal if all properties match")
    void shouldConsiderEventsEqualIfPropertiesMatch() {
        // Given
        Path filePath = Paths.get("/tmp/test.zip");
        FileEvent event1 = FileEvent.create(filePath, "test.zip", FileType.ZIP);
        FileEvent event2 = FileEvent.create(filePath, "test.zip", FileType.ZIP);

        // Then
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("Should consider events different if file name differs")
    void shouldConsiderEventsDifferentIfFileNameDiffers() {
        // Given
        Path filePath = Paths.get("/tmp/test.zip");
        FileEvent event1 = FileEvent.create(filePath, "file1.zip", FileType.ZIP);
        FileEvent event2 = FileEvent.create(filePath, "file2.zip", FileType.ZIP);

        // Then
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    @DisplayName("Should consider events different if file type differs")
    void shouldConsiderEventsDifferentIfFileTypeDiffers() {
        // Given
        Path filePath = Paths.get("/tmp/test.zip");
        FileEvent event1 = FileEvent.create(filePath, "test.zip", FileType.ZIP);
        FileEvent event2 = FileEvent.create(filePath, "test.zip", FileType.TXT);

        // Then
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    @DisplayName("Should return same instance for same path and name with same type")
    void shouldReturnMeaningfulToString() {
        // Given
        Path filePath = Paths.get("/tmp", "document.txt");
        FileEvent event = FileEvent.create(filePath, "document.txt", FileType.TXT);

        // When
        String result = event.toString();

        // Then
        assertThat(result).contains("document.txt");
        assertThat(result).contains("TXT");
    }
}
