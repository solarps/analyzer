package com.duop.analyzer;

import com.duop.analyzer.sheets.SheetsService;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class AnalyzerApplication implements CommandLineRunner {

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
            System.out.println("No files found.");
        } else {
            for (File file : files) {
                sheetsService.readFile(file);
            }
        }
    }
}
