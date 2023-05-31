package com.duop.analyzer.sheets;

import com.duop.analyzer.aspects.LogReadingTime;
import com.duop.analyzer.entity.*;
import com.duop.analyzer.sheets.reader.SpreadsheetsReader;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    @Value("${controlLectorCell}")
    private String controlLectorCell;
    @Value("${subjectCell}")
    private String subjectCell;
    @Value("${sheets.typeCell}")
    private String sheetTypeCell;
    @Value("${sheets.yearCell}")
    private String sheetYearCell;
    @Value("${sheets.numberCell}")
    private String sheetNumberCell;
    @Value("${sheets.courseCell}")
    private String sheetCourseCell;
    @Value("${sheets.postingDateCell}")
    private String sheetPostingDateCell;
    @Value("${sheets.facultyCell}")
    private String sheetFacultyCell;

    @LogReadingTime
    public SheetReadResult readFile(File file, SpreadsheetsReader reader) throws IOException {
        logger.info("ReaderService: read file \"{}\"", file.getId());
        return getSheetReadResult(file, reader);
    }

    private SheetReadResult getSheetReadResult(File file, SpreadsheetsReader reader)
            throws IOException {
        Lector lector = readLectorFromFile(file, lectorCell, reader);
        Lector controlLector = readLectorFromFile(file, controlLectorCell, reader);
        Subject subject = readSubjectFromFile(file, subjectCell, reader);
        Sheet sheet =
                Sheet.builder()
                        .number(readSheetNumberFromFile(file, sheetNumberCell, reader))
                        .faculty(readFacultyFromFile(file, sheetFacultyCell, reader))
                        .sheetType(readSheetTypeFromFile(file, sheetTypeCell, reader))
                        .year(readSheetYearFromFile(file, sheetYearCell, reader))
                        .course(readSheetCourseFromFile(file, sheetCourseCell, reader))
                        .postingDate(readPostingDateFromFile(file, sheetPostingDateCell, reader))
                        .build();
        Map<Student, Mark> studentsMarks =
                getStudentsWithMarksFromFile(
                        file, reader.getRangeForTable(file.getId(), startCell, endColumn), reader);
        Group group = getStudentGroupFromFile(file, groupCell, reader);
        return new SheetReadResult(sheet, studentsMarks, lector, controlLector, group, subject);
    }

    private String readFacultyFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read faculty from file \"{}\" in range \"{}\"", file.getId(), range);
        return reader.readMergedCell(file.getId(), range);
    }

    private LocalDate readPostingDateFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read posting date from file \"{}\" in range \"{}\"", file.getId(), range);
        List<String> dateInRange = reader.readInRange(file.getId(), range).get(0);
        Pattern yearPattern = Pattern.compile("\\d{4}");
        Matcher matcher = yearPattern.matcher(dateInRange.get(1));
        String year;
        if (matcher.find()) {
            year = matcher.group(0);
        } else {
            logger.error("No posting year found");
            throw new IllegalArgumentException("No posting year found");
        }

        String dateString =
                dateInRange.get(0).strip().replace("\"", "").concat(" " + year).replaceAll(" +", " ");
        List<DateTimeFormatter> formatters =
                List.of(
                        DateTimeFormatter.ofPattern("d MM yyyy"),
                        DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("uk")));
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        logger.error("Date could not be parsed");
        throw new IllegalArgumentException("Date could not be parsed");
    }

    private Integer readSheetNumberFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read sheet number from file \"{}\" in range \"{}\"", file.getId(), range);
        return Double.valueOf(reader.readCell(file.getId(), range)).intValue();
    }

    private Short readSheetYearFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read sheet year from file \"{}\" in range \"{}\"", file.getId(), range);
        return Double.valueOf(reader.readCell(file.getId(), range)).shortValue();
    }

    private SheetType readSheetTypeFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read sheet type from file \"{}\" in range \"{}\"", file.getId(), range);
        return SheetType.valueOf(reader.readCell(file.getId(), range));
    }

    private Subject readSubjectFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read subject from file \"{}\" in range \"{}\"", file.getId(), range);
        String name = reader.readMergedCell(file.getId(), range);
        return new Subject(name);
    }

    private Lector readLectorFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read lector from file \"{}\" in range \"{}\"", file.getId(), range);
        String name = reader.readMergedCell(file.getId(), range);
        return new Lector(name);
    }

    private Map<Student, Mark> getStudentsWithMarksFromFile(
            File file, String range, SpreadsheetsReader reader) throws IOException {
        logger.trace(
                "ReaderService: read students from file \"{}\" in range \"{}\"", file.getId(), range);
        List<List<String>> resultList = reader.readInRangeExcludedFields(file.getId(), range, 2, 3);
        return resultList.stream()
                .collect(
                        Collectors.toMap(
                                row -> Student.builder().details(StudentDetails.builder().name(row.get(0)).build()).build(),
                                row -> new Mark(Double.valueOf(row.get(1)).intValue())));
    }

    private byte readSheetCourseFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read student course from file \"{}\" in range \"{}\"", file.getId(), range);
        return Double.valueOf(reader.readCell(file.getId(), range)).byteValue();
    }

    private Group getStudentGroupFromFile(File file, String range, SpreadsheetsReader reader)
            throws IOException {
        logger.trace(
                "ReaderService: read student group from file \"{}\" in range \"{}\"", file.getId(), range);
        String result = reader.readCell(file.getId(), range);
        Pattern pattern = Pattern.compile("(?<letters>[А-Яа-яA-Za-z]+)?-?(?<numbers>\\d+)");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            String number = matcher.group("numbers");
            String letters = matcher.group("letters");
            return Group.builder().name(letters).number(Short.valueOf(number)).build();
        } else throw new IllegalArgumentException();
    }
}
