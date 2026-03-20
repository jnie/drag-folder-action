package dk.jnie.dragfolder;

import dk.jnie.dragfolder.domain.outbound.FileHandler;
import dk.jnie.dragfolder.domain.outbound.FileSystemMonitor;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import dk.jnie.dragfolder.inbound.cli.CliRunner;
import dk.jnie.dragfolder.outbound.filesystem.WatchServiceFileSystemMonitor;
import dk.jnie.dragfolder.outbound.zip.ZipFileHandler;
import dk.jnie.dragfolder.service.FileProcessingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Value("${monitor.folder:/tmp/monitor}")
    private String monitorFolder;

    @Value("${output.folder:/tmp/output}")
    private String outputFolder;

    @Value("${timer.seconds:5}")
    private int timerSeconds;

    @Value("${clear.folder:false}")
    private boolean clearFolder;

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
    @ConditionalOnProperty(name = "cli.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner runner(FileProcessingService fileProcessingService) {
        return args -> {
            log.info("Starting DragFolderAction with config:");
            log.info("  Monitor folder: {}", monitorFolder);
            log.info("  Output folder: {}", outputFolder);
            log.info("  Timer: {} seconds", timerSeconds);
            log.info("  Clear folder: {}", clearFolder);

            File monitorDir = new File(monitorFolder);
            if (!monitorDir.exists() || !monitorDir.isDirectory()) {
                log.error("Monitor folder does not exist or is not a directory: {}", monitorFolder);
                log.info("Please create the monitor folder before starting the application.");
                log.info("Example: mkdir -p {}", monitorFolder);
                return;
            }

            File outputDir = new File(outputFolder);
            if (!outputDir.exists() || !outputDir.isDirectory()) {
                log.warn("Output folder does not exist: {}. It will be created automatically.", outputFolder);
            }

            CliRunner cliRunner = new CliRunner(fileProcessingService);
            cliRunner.run(monitorFolder, outputFolder, timerSeconds, clearFolder);
        };
    }
}