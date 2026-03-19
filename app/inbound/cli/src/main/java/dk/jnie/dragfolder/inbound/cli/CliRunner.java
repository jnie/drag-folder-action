package dk.jnie.dragfolder.inbound.cli;

import dk.jnie.dragfolder.domain.model.Configuration;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class CliRunner {
    private static final Logger log = LoggerFactory.getLogger(CliRunner.class);

    private final FileProcessingService fileProcessingService;

    public CliRunner(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    public void run(String monitorFolder, String outputFolder, int timerSeconds, boolean clearFolder) {
        log.info("Starting DragFolderAction CLI");

        Configuration config = Configuration.create(
                monitorFolder,
                outputFolder,
                timerSeconds,
                clearFolder);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down...");
            fileProcessingService.stopMonitoring();
        }));

        fileProcessingService.startMonitoring(config);

        log.info("Monitoring started. Press Enter to stop.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        fileProcessingService.stopMonitoring();
        log.info("Stopped");
    }
}