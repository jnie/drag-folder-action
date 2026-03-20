package dk.jnie.dragfolder.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Configuration Tests")
class ConfigurationTest {

    @Test
    @DisplayName("Should create configuration with all properties")
    void shouldCreateConfigurationWithAllProperties() {
        // Given
        String monitorFolder = "/tmp/monitor";
        String outputFolder = "/tmp/output";
        int timerSeconds = 10;
        boolean clearFolder = true;

        // When
        Configuration config = Configuration.create(monitorFolder, outputFolder, timerSeconds, clearFolder);

        // Then
        assertThat(config.getMonitorFolder()).isEqualTo(monitorFolder);
        assertThat(config.getOutputFolder()).isEqualTo(outputFolder);
        assertThat(config.getTimerSeconds()).isEqualTo(timerSeconds);
        assertThat(config.getClearMonitorFolder()).isTrue();
    }

    @Test
    @DisplayName("Should create configuration with clear folder false")
    void shouldCreateConfigurationWithClearFolderFalse() {
        // Given
        String monitorFolder = "/home/user/downloads";
        String outputFolder = "/home/user/processed";
        int timerSeconds = 5;
        boolean clearFolder = false;

        // When
        Configuration config = Configuration.create(monitorFolder, outputFolder, timerSeconds, clearFolder);

        // Then
        assertThat(config.getClearMonitorFolder()).isFalse();
    }

    @Test
    @DisplayName("Should consider two configurations equal if all properties match")
    void shouldConsiderConfigurationsEqualIfPropertiesMatch() {
        // Given
        Configuration config1 = Configuration.create("/tmp/monitor", "/tmp/output", 5, false);
        Configuration config2 = Configuration.create("/tmp/monitor", "/tmp/output", 5, false);

        // Then
        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }

    @Test
    @DisplayName("Should consider configurations different if monitor folder differs")
    void shouldConsiderConfigurationsDifferentIfMonitorFolderDiffers() {
        // Given
        Configuration config1 = Configuration.create("/tmp/monitor1", "/tmp/output", 5, false);
        Configuration config2 = Configuration.create("/tmp/monitor2", "/tmp/output", 5, false);

        // Then
        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    @DisplayName("Should consider configurations different if timer seconds differ")
    void shouldConsiderConfigurationsDifferentIfTimerSecondsDiffer() {
        // Given
        Configuration config1 = Configuration.create("/tmp/monitor", "/tmp/output", 5, false);
        Configuration config2 = Configuration.create("/tmp/monitor", "/tmp/output", 10, false);

        // Then
        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    @DisplayName("Should return meaningful toString representation")
    void shouldReturnMeaningfulToString() {
        // Given
        Configuration config = Configuration.create("/tmp/monitor", "/tmp/output", 5, false);

        // When
        String result = config.toString();

        // Then
        assertThat(result).contains("monitorFolder='/tmp/monitor'");
        assertThat(result).contains("outputFolder='/tmp/output'");
        assertThat(result).contains("timerSeconds=5");
        assertThat(result).contains("clearMonitorFolder=false");
    }
}
