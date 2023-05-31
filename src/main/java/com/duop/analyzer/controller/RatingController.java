package com.duop.analyzer.controller;

import com.duop.analyzer.service.GroupService;
import com.duop.analyzer.sheets.DriveService;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequiredArgsConstructor
public class RatingController {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");
    private final DriveService driveService;
    private final GroupService groupService;

    @Value("#{'${searchMimetypes}'.split(',')}")
    private List<String> mimeTypes;


    @PostMapping("/generateRatings")
    public String generateRatings(@RequestParam(name = "folderId") String folderId, RedirectAttributes redirectAttributes) throws GeneralSecurityException, IOException, InterruptedException {
        try {
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
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", "Error");
            return "redirect:/index.html";
        }
        redirectAttributes.addAttribute("message", "Success");
        return "redirect:/index.html";
    }
}
