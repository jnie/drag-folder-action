package dk.jnie.dragfolder.service;

import dk.jnie.dragfolder.domain.model.Configuration;
import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import dk.jnie.dragfolder.domain.outbound.FileHandler;
import dk.jnie.dragfolder.domain.outbound.FileSystemMonitor;
import dk.jnie.dragfolder.domain.services.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FileProcessingServiceImpl implements FileProcessingService {
    private static final Logger log = LoggerFactory.getLogger(FileProcessingServiceImpl.class);

    private final FileSystemMonitor fileSystemMonitor;
    private final List<FileHandler> handlers;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Path outputPath;

    public FileProcessingServiceImpl(FileSystemMonitor fileSystemMonitor, List<FileHandler> handlers) {
        this.fileSystemMonitor = fileSystemMonitor;
        this.handlers = new ArrayList<>(handlers);
    }

    @Override
    public void startMonitoring(Configuration config) {
        if (running.getAndSet(true)) {
            log.warn("Already monitoring");
            return;
        }

        Path monitorPath = Path.of(config.getMonitorFolder());
        if (!Files.isDirectory(monitorPath)) {
            log.error("Monitor folder does not exist: {}", monitorPath);
            running.set(false);
            return;
        }

        this.outputPath = Path.of(config.getOutputFolder());
        log.info("Starting to monitor folder: {}", monitorPath);
        int timeoutSeconds = config.getTimerSeconds();
        fileSystemMonitor.startMonitoring(monitorPath, this::processFile, timeoutSeconds);
    }

    @Override
    public void stopMonitoring() {
        log.info("Stopping monitoring");
        running.set(false);
        fileSystemMonitor.stopMonitoring();
    }

    @Override
    public void processFile(FileEvent fileEvent) {
        log.info("Processing file: {}", fileEvent.getFileName());

        Optional<FileHandler> handler = handlers.stream()
                .filter(h -> h.canHandle(fileEvent))
                .findFirst();

        if (handler.isPresent()) {
            handler.get().handle(fileEvent, outputPath);
        } else {
            log.warn("No handler found for file type: {}. " +
                     "Available handlers: {} supporting: {}",
                     fileEvent.getFileType(),
                     handlers.stream()
                         .map(h -> h.getClass().getSimpleName())
                         .collect(Collectors.joining(", ")),
                     getAllSupportedTypes());
        }
    }

    private String getAllSupportedTypes() {
        return handlers.stream()
                .flatMap(h -> h.getSupportedTypes().stream())
                .map(FileType::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public List<FileEvent> checkForNewFiles(Path folderPath) {
        return fileSystemMonitor.getNewFiles(folderPath);
    }
}