package com.duop.analyzer.sheets;

import com.duop.analyzer.sheets.reader.ExcelReader;
import com.duop.analyzer.sheets.reader.GoogleSheetsReader;
import com.duop.analyzer.sheets.reader.SpreadsheetsReader;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveService {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");
    private final ReaderService readerService;
    private final SheetResultProcessor sheetResultProcessor;

    @Value("${sheetsMimetype}")
    private String sheetsMimetype;

    @Value("${excelMimetype}")
    private String excelMimetype;

    public List<File> getAllFiles(String folderId, List<String> mimeTypes) throws GeneralSecurityException, IOException {
        String query = buildQuery(folderId, mimeTypes);
        logger.info("SheetsService: get all files");
        Drive drive = GoogleServicesUtil.getDriverService();
        FileList result = drive.files().list().setQ(query).execute();
        return result.getFiles();
    }

    // "('" + folderId + "' in parents) and (mimeType='" + mimeType1 + "' or mimeType='" + mimeType2 +
    // "') and (trashed = false)"
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
            logger.info("DriveService: read google sheets file \"{}\"", file.getId());
            Sheets sheets = GoogleServicesUtil.getSheetsService();
            SpreadsheetsReader reader = new GoogleSheetsReader(sheets);
            sheetResultProcessor.saveResult(readerService.readFile(file, reader));
        } else if (file.getMimeType().equals(excelMimetype)) {
            logger.info("DriveService: read excel file \"{}\"", file.getId());
            Drive drive = GoogleServicesUtil.getDriverService();
            InputStream inputStream = drive.files().get(file.getId()).executeMediaAsInputStream();
            SpreadsheetsReader reader = new ExcelReader(new XSSFWorkbook(inputStream));
            sheetResultProcessor.saveResult(readerService.readFile(file, reader));
        } else {
            logger.error("SheetsService: unsupported file format for file:{}", file.getId());
        }
    }
}
