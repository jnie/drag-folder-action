package dk.jnie.dragfolder.outbound.zip;

import dk.jnie.dragfolder.domain.model.FileEvent;
import dk.jnie.dragfolder.domain.model.FileType;
import dk.jnie.dragfolder.domain.outbound.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileHandler implements FileHandler {
    private static final Logger log = LoggerFactory.getLogger(ZipFileHandler.class);

    @Override
    public boolean canHandle(FileEvent fileEvent) {
        return fileEvent.getFileType() == FileType.ZIP;
    }

    @Override
    public void handle(FileEvent fileEvent, Path outputFolder) {
        File zipFile = fileEvent.getFilePath().toFile();
        String outputDir = outputFolder.resolve(extractBaseName(zipFile.getName())).toString();
        File outputDirFile = new File(outputDir);

        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }

        try (ZipFile zf = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    new File(outputDir, entry.getName()).mkdirs();
                } else {
                    extractEntry(zf, entry, outputDir);
                }
            }
            log.info("Extracted {} to {}", zipFile.getName(), outputDir);
        } catch (IOException e) {
            log.error("Failed to extract zip file: {}", e.getMessage());
        }
    }

    private void extractEntry(ZipFile zipFile, ZipEntry entry, String outputDir) throws IOException {
        File outputFile = new File(outputDir, entry.getName());
        try (InputStream is = zipFile.getInputStream(entry);
             OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        }
    }

    private String extractBaseName(String fileName) {
        if (fileName.endsWith(".zip")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
}