package com.duop.analyzer;

import com.duop.analyzer.sheets.SheetsService;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class AnalyzerApplication implements CommandLineRunner {

    @Autowired
    private SheetsService sheetsService;

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String folderId = "1zKPJwmNcQCNYTzZGxTuBTgLuJTanRhA5";

        // Устанавливаем фильтр по типу файла
        String mimeType1 = "application/vnd.google-apps.spreadsheet";
        String mimeType2 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        List<File> files = sheetsService.getAllFiles(folderId, List.of(mimeType1, mimeType2));

        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            for (File file : files) {
                sheetsService.readFile(file);
            }
        }
    }
}
