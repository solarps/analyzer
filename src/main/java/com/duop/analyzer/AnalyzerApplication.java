package com.duop.analyzer;

import com.duop.analyzer.service.GroupService;
import com.duop.analyzer.service.StudentService;
import com.duop.analyzer.sheets.DriveService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class AnalyzerApplication implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");
    @Autowired
    private DriveService driveService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private GroupService groupService;

    @Value("#{'${searchMimetypes}'.split(',')}")
    private List<String> mimeTypes;

    @Value("${searchFolderId}")
    private String folderId;

    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Callable<String>> tasks = new ArrayList<>();
        List<File> files = driveService.getAllFiles(folderId, mimeTypes);
        ExecutorService service = Executors.newFixedThreadPool(5);
        if (files == null || files.isEmpty()) {
            logger.warn("No files found.");
        } else {
            for (File file : files) {
                tasks.add(() -> {
                    try {
                        driveService.readFile(file);
                        return "done";
                    } catch (IOException | GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            service.invokeAll(tasks);
        }
        logger.info("Writing ratings");
        List<String> flows = groupService.getAllUniversityFlows();
        String folder = driveService.createFolder(folderId, "Рейтинги");
        for (String flow : flows) {
            driveService.writeRatingFile(flow, folder);
        }
        service.shutdown();
    }
}
