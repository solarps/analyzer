package com.duop.analyzer.sheets;

import com.duop.analyzer.entity.Lector;
import com.duop.analyzer.entity.Mark;
import com.duop.analyzer.entity.Student;
import com.duop.analyzer.entity.Subject;
import com.duop.analyzer.repository.LectorRepository;
import com.duop.analyzer.repository.MarkRepository;
import com.duop.analyzer.repository.StudentRepository;
import com.duop.analyzer.repository.SubjectRepository;
import com.duop.analyzer.sheets.reader.ExcelReader;
import com.duop.analyzer.sheets.reader.GoogleSheetsReader;
import com.duop.analyzer.sheets.reader.SpreadsheetsReader;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {
    @Value("${students.startCell}")
    private String startCell;
    @Value("${students.endColumn}")
    private String endColumn;
    @Value("${groupCell}")
    private String groupCell;
    @Value("${lectorCell}")
    private String lectorCell;
    @Value("${subjectCell}")
    private String subjectCell;
    @Value("${courseCell}")
    private String courseCell;
    private final StudentRepository studentRepository;
    private final LectorRepository lectorRepository;
    private final SubjectRepository subjectRepository;
    private final MarkRepository markRepository;

    private record Result(String name, String mark) {
    }

    public void readExcelFile(File file) throws IOException, GeneralSecurityException {
        Drive drive = GoogleServicesUtil.getDriverService();
        InputStream inputStream = drive.files().get(file.getId()).executeMediaAsInputStream();
        SpreadsheetsReader reader = new ExcelReader(new XSSFWorkbook(inputStream));
        readFile(file, reader);
    }

    public void readGoogleSheetsFile(File file) throws GeneralSecurityException, IOException {
        Sheets sheets = GoogleServicesUtil.getSheetsService();
        SpreadsheetsReader reader = new GoogleSheetsReader(sheets);
        readFile(file, reader);
    }

    private void readFile(File file, SpreadsheetsReader reader) throws IOException {
        Lector lector = getLectorFromFile(file, lectorCell, reader);
        Subject subject = getSubjectFromFile(file, subjectCell, reader);
        subject.setLector(lector);
        lectorRepository.save(lector);
        subjectRepository.save(subject);
        List<Result> results = getStudentsFromFile(file, reader.getRangeForTable(file.getId(), startCell, endColumn), reader);
        String group = getStudentGroupFromFile(file, groupCell, reader);
        int studentCourse = getStudentCourseFromFile(file, courseCell, reader);
        for (Result result : results) {
            Optional<Student> optionalStudent = studentRepository.findStudentByUniqueKey(result.name, group);
            Mark.MarkId markId = new Mark.MarkId();
            if (optionalStudent.isPresent()) {
                markId.setStudent(optionalStudent.get());
                markId.setSubject(subject);
            } else {
                Student student = new Student(studentCourse, result.name, group);
                studentRepository.save(student);
                markId.setStudent(student);
                markId.setSubject(subject);
            }
            if (!markRepository.existsById(markId)) {
                Mark mark = new Mark(Double.valueOf(result.mark).intValue());
                mark.setId(markId);
                markRepository.save(mark);
            }
        }
    }

    private Subject getSubjectFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        String name = reader.readMergedCell(file.getId(), range);
        Optional<Subject> optionalSubject = subjectRepository.findByName(name);
        return optionalSubject.orElse(new Subject(name));
    }

    private Lector getLectorFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        String name = reader.readMergedCell(file.getId(), range);
        Optional<Lector> optionalLector = lectorRepository.findByName(name);
        return optionalLector.orElse(new Lector(name));
    }

    private List<Result> getStudentsFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        List<List<String>> resultList = reader.readInRangeExcludedFields(file.getId(), range, 2, 3);
        return resultList.stream().map(list -> new Result(list.get(0), list.get(1))).toList();
    }

    private int getStudentCourseFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        return Double.valueOf(reader.readCell(file.getId(), range)).intValue();
    }

    private String getStudentGroupFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        return reader.readCell(file.getId(), range);
    }
}
