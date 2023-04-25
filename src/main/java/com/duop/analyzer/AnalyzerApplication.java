package com.duop.analyzer;

import com.duop.analyzer.sheets.SheetsService;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class AnalyzerApplication implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");
    @Autowired
    private SheetsService sheetsService;
    @Value("#{'${searchMimetypes}'.split(',')}")
    private List<String> mimeTypes;
    @Value("${searchFolderId}")
    private String folderId;

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<File> files = sheetsService.getAllFiles(folderId, mimeTypes);
        if (files == null || files.isEmpty()) {
            logger.warn("No files found.");
        } else {
            ExecutorService service = Executors.newFixedThreadPool(4);
            for (File file : files) {
                service.execute(()-> {
                    try {
                        sheetsService.readFile(file);
                    } catch (IOException | GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            service.shutdown();
        }
    }
}
