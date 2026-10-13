package com.analyzer.seo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
public class LocalScreenshotService {

    private static final Logger log = LoggerFactory.getLogger(LocalScreenshotService.class);
    private static final Path SCREENSHOTS_DIR = Paths.get("data", "screenshots");

    public LocalScreenshotService() {
        try {
            if (!Files.exists(SCREENSHOTS_DIR)) {
                Files.createDirectories(SCREENSHOTS_DIR);
            }
        } catch (Exception e) {
            log.warn("Impossibile creare la cartella data/screenshots: {}", e.getMessage());
        }
    }

    /**
     * Esegue lo script Node.js capture.js in background con Chrome/Edge nativo.
     * Restituisce l'URL relativo /screenshots/{jobId}.jpg se generato con successo,
     * oppure null in caso di fallimento (attivando così la catena di fallback).
     */
    public String captureScreenshot(String jobId, String targetUrl) {
        String filename = jobId + ".jpg";
        Path outputPath = SCREENSHOTS_DIR.resolve(filename);

        File scriptFile = new File("capture.js");
        if (!scriptFile.exists()) {
            log.warn("Script capture.js non trovato.");
            return null;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "node",
                    scriptFile.getAbsolutePath(),
                    targetUrl,
                    outputPath.toAbsolutePath().toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean finished = process.waitFor(18, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0 && Files.exists(outputPath) && Files.size(outputPath) > 1000) {
                log.info("Screenshot nativo generato con successo per {}: {} bytes", targetUrl, Files.size(outputPath));
                return "/screenshots/" + filename;
            } else {
                if (!finished) {
                    process.destroyForcibly();
                    log.warn("Processo di screenshot scaduto dopo 18s per {}", targetUrl);
                } else {
                    log.warn("Processo di screenshot terminato con codice {}", process.exitValue());
                }
            }
        } catch (Exception e) {
            log.warn("Errore durante l'esecuzione di capture.js: {}", e.getMessage());
        }

        return null;
    }
}
