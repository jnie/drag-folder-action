package dk.jnie.dragfolder.domain.model;

import java.util.Objects;

public final class Configuration {
    private final String monitorFolder;
    private final String outputFolder;
    private final int timerSeconds;
    private final boolean clearMonitorFolder;

    private Configuration(String monitorFolder, String outputFolder, int timerSeconds, boolean clearMonitorFolder) {
        this.monitorFolder = monitorFolder;
        this.outputFolder = outputFolder;
        this.timerSeconds = timerSeconds;
        this.clearMonitorFolder = clearMonitorFolder;
    }

    public static Configuration create(String monitorFolder, String outputFolder, int timerSeconds, boolean clearMonitorFolder) {
        return new Configuration(monitorFolder, outputFolder, timerSeconds, clearMonitorFolder);
    }

    public String getMonitorFolder() {
        return monitorFolder;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public int getTimerSeconds() {
        return timerSeconds;
    }

    public boolean getClearMonitorFolder() {
        return clearMonitorFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return timerSeconds == that.timerSeconds &&
                clearMonitorFolder == that.clearMonitorFolder &&
                Objects.equals(monitorFolder, that.monitorFolder) &&
                Objects.equals(outputFolder, that.outputFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monitorFolder, outputFolder, timerSeconds, clearMonitorFolder);
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "monitorFolder='" + monitorFolder + '\'' +
                ", outputFolder='" + outputFolder + '\'' +
                ", timerSeconds=" + timerSeconds +
                ", clearMonitorFolder=" + clearMonitorFolder +
                '}';
    }
}