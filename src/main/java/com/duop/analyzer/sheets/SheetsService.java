package com.duop.analyzer.sheets;

import com.duop.analyzer.entity.Student;
import com.duop.analyzer.sheets.reader.ExcelReader;
import com.duop.analyzer.sheets.reader.GoogleSheetsReader;
import com.duop.analyzer.sheets.reader.SpreadsheetsReader;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;

@Service
public class SheetsService {

    public List<File> getAllFiles(String folderId, List<String> mimeTypes) throws GeneralSecurityException, IOException {
        String query = buildQuery(folderId, mimeTypes);

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
        // Build a new authorized API client service.
        if (file.getMimeType().equals("application/vnd.google-apps.spreadsheet")) {
            Student student = new Student();
            SpreadsheetsReader reader = new GoogleSheetsReader(GoogleServicesUtil.getSheetsService());
            student.setCourse(getStudentCourse(file, "H6:H6", reader));

        } else if (file.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            Drive drive = GoogleServicesUtil.getDriverService();
            InputStream inputStream = drive.files().get(file.getId()).executeMediaAsInputStream();
            SpreadsheetsReader reader = new ExcelReader(new XSSFWorkbook(inputStream));
            Student student = new Student();
            student.setCourse(getStudentCourse(file, "H6", reader));

        } else {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }

    private int getStudentCourse(File file, String s, SpreadsheetsReader reader) throws IOException {
        return Integer.parseInt(reader.readCell(file.getId(), s));
    }

}
