package dk.jnie.dragfolder;

import dk.jnie.dragfolder.domain.model.FileHandler;
import dk.jnie.dragfolder.domain.outbound.FileSystemMonitor;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import dk.jnie.dragfolder.inbound.cli.CliRunner;
import dk.jnie.dragfolder.outbound.filesystem.WatchServiceFileSystemMonitor;
import dk.jnie.dragfolder.outbound.zip.ZipFileHandler;
import dk.jnie.dragfolder.service.FileProcessingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FileSystemMonitor fileSystemMonitor() {
        return new WatchServiceFileSystemMonitor();
    }

    @Bean
    public FileHandler zipFileHandler() {
        return new ZipFileHandler();
    }

    @Bean
    public FileProcessingService fileProcessingService(
            FileSystemMonitor fileSystemMonitor,
            List<FileHandler> handlers) {
        return new FileProcessingServiceImpl(fileSystemMonitor, handlers);
    }

    @Bean
    public CommandLineRunner runner(FileProcessingService fileProcessingService) {
        return args -> {
            String monitorFolder = System.getProperty("monitor.folder", "/tmp/monitor");
            String outputFolder = System.getProperty("output.folder", "/tmp/output");
            int timerSeconds = Integer.parseInt(System.getProperty("timer.seconds", "5"));
            boolean clearFolder = Boolean.parseBoolean(System.getProperty("clear.folder", "false"));

            log.info("Starting DragFolderAction with config:");
            log.info("  Monitor folder: {}", monitorFolder);
            log.info("  Output folder: {}", outputFolder);
            log.info("  Timer: {} seconds", timerSeconds);
            log.info("  Clear folder: {}", clearFolder);

            CliRunner cliRunner = new CliRunner(fileProcessingService);
            cliRunner.run(monitorFolder, outputFolder, timerSeconds, clearFolder);
        };
    }
}