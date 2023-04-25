package com.duop.analyzer.sheets;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SheetsService {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");
    @Value("${sheetsMimetype}")
    private String sheetsMimetype;
    @Value("${excelMimetype}")
    private String excelMimetype;
    private final ReaderService readerService;

    public List<File> getAllFiles(String folderId, List<String> mimeTypes) throws GeneralSecurityException, IOException {
        String query = buildQuery(folderId, mimeTypes);
        logger.info("SheetsService: get all files");
        Drive drive = GoogleServicesUtil.getDriverService();
        FileList result = drive.files().list().setQ(query).execute();
        return result.getFiles();
    }

    //"('" + folderId + "' in parents) and (mimeType='" + mimeType1 + "' or mimeType='" + mimeType2 + "') and (trashed = false)"
    private String buildQuery(String folderId, List<String> mimeTypes) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("('").append(folderId).append("' in parents)");
        if (!mimeTypes.isEmpty()) {
            queryBuilder.append(" and (");
            Iterator<String> iterator = mimeTypes.iterator();
            while (iterator.hasNext()) {
                String mimeType = iterator.next();
                queryBuilder.append("mimeType='").append(mimeType).append("'");
                if (iterator.hasNext()) {
                    queryBuilder.append(" or ");
                }
            }
            queryBuilder.append(")");
        }
        queryBuilder.append(" and (trashed = false)");
        return queryBuilder.toString();
    }

    public void readFile(File file) throws IOException, GeneralSecurityException {
        if (file.getMimeType().equals(sheetsMimetype)) {
            readerService.readGoogleSheetsFile(file);
        } else if (file.getMimeType().equals(excelMimetype)) {
            readerService.readExcelFile(file);
        } else {
            logger.error("SheetsService: unsupported file format");
            throw new IllegalArgumentException("Unsupported file format");
        }
    }
}
