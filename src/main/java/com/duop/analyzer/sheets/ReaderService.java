package com.duop.analyzer.sheets;

import com.duop.analyzer.aspects.LogReadingTime;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");
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

    @LogReadingTime
    public void readExcelFile(File file) throws IOException, GeneralSecurityException {
        logger.info("ReaderService: read excel file \"{}\"", file.getId());
        Drive drive = GoogleServicesUtil.getDriverService();
        InputStream inputStream = drive.files().get(file.getId()).executeMediaAsInputStream();
        SpreadsheetsReader reader = new ExcelReader(new XSSFWorkbook(inputStream));
        readFile(file, reader);
    }

    @LogReadingTime
    public void readGoogleSheetsFile(File file) throws GeneralSecurityException, IOException {
        logger.info("ReaderService: read google sheets file \"{}\"", file.getId());
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
        List<Student> students = new ArrayList<>();
        List<Mark> marks = new ArrayList<>();
        for (Result result : results) {
            Optional<Student> optionalStudent = studentRepository.findStudentByUniqueKey(result.name, group);
            Mark.MarkId markId = new Mark.MarkId();
            Mark mark = new Mark(Double.valueOf(result.mark).intValue());
            if (optionalStudent.isPresent()) {
                markId.setStudentId(optionalStudent.get().getId());
                markId.setSubjectId(subject.getId());
                mark.setStudent(optionalStudent.get());
            } else {
                Student student = new Student(studentCourse, result.name, group);
                students.add(student);
                markId.setStudentId(student.getId());
                markId.setSubjectId(subject.getId());
                mark.setStudent(student);
            }
            if (!markRepository.existsById(markId)) {
                mark.setSubject(subject);
                mark.setId(markId);
                marks.add(mark);
            }
        }
        studentRepository.saveAll(students);
        markRepository.saveAll(marks);
    }

    private Subject getSubjectFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        logger.trace("ReaderService: read subject from file \"{}\" in range \"{}\"", file.getId(), range);
        String name = reader.readMergedCell(file.getId(), range);
        Optional<Subject> optionalSubject = subjectRepository.findByName(name);
        return optionalSubject.orElse(new Subject(name));
    }

    private Lector getLectorFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        logger.trace("ReaderService: read lector from file \"{}\" in range \"{}\"", file.getId(), range);
        String name = reader.readMergedCell(file.getId(), range);
        Optional<Lector> optionalLector = lectorRepository.findByName(name);
        return optionalLector.orElse(new Lector(name));
    }

    private List<Result> getStudentsFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        logger.trace("ReaderService: read students from file \"{}\" in range \"{}\"", file.getId(), range);
        List<List<String>> resultList = reader.readInRangeExcludedFields(file.getId(), range, 2, 3);
        return resultList.stream().map(list -> new Result(list.get(0), list.get(1))).toList();
    }

    private int getStudentCourseFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        logger.trace("ReaderService: read student course from file \"{}\" in range \"{}\"", file.getId(), range);
        return Double.valueOf(reader.readCell(file.getId(), range)).intValue();
    }

    private String getStudentGroupFromFile(File file, String range, SpreadsheetsReader reader) throws IOException {
        logger.trace("ReaderService: read student group from file \"{}\" in range \"{}\"", file.getId(), range);
        return reader.readCell(file.getId(), range);
    }
}
