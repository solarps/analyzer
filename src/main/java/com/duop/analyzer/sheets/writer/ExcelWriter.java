package com.duop.analyzer.sheets.writer;

import com.duop.analyzer.entity.Student;
import com.duop.analyzer.repository.SubjectMark;
import com.duop.analyzer.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ExcelWriter implements StudentRatingWriter {
    private final RatingService ratingService;
    private Workbook workbook;
    private int[] subjectColumns;
    private int examCell;
    private int courseCell;
    private int testCell;
    private int ratingCell;

    @Override
    public byte[] writeStudentRating(Map<Student, List<SubjectMark>> studentMarks) throws IOException {
        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rating");

        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(100);
        writeHeader(headerRow, studentMarks);

        writeStudents(sheet, studentMarks);

        sheet.setColumnWidth(0, 4 * 256);
        sheet.setColumnWidth(1, 40 * 256);
        sheet.setColumnWidth(2, 8 * 256);
        sheet.setColumnWidth(3, 4 * 256);
        sheet.setColumnWidth(4, 7 * 256);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return outputStream.toByteArray();
    }

    private void writeStudents(Sheet sheet, Map<Student, List<SubjectMark>> studentMarks) {
        int lastRow = 3;
        int number = 1;
        studentMarks = ratingService.computeRating(studentMarks);
        for (Map.Entry<Student, List<SubjectMark>> entry : studentMarks.entrySet()) {
            int lastCell = 0;
            Row row = sheet.createRow(lastRow);
            row.createCell(lastCell++).setCellValue(number++);
            row.createCell(lastCell++).setCellValue(entry.getKey().getDetails().getName());
            row.createCell(lastCell++).setCellValue(entry.getKey().getGroup().toString());
            row.createCell(lastCell).setCellValue(entry.getKey().getEducationType().type);
            writeMarks(sheet, entry.getValue(), row);
            if (courseCell != 0) {
                BigDecimal courseMark = getAvgMark(entry.getValue(), List.of("КР"));
                row.createCell(courseCell).setCellValue(courseMark.doubleValue());
            }
            if (examCell != 0) {
                BigDecimal examMark = getAvgMark(entry.getValue(), List.of("ІСП"));
                row.createCell(examCell).setCellValue(examMark.doubleValue());
            }
            if (testCell != 0) {
                BigDecimal testMark = getAvgMark(entry.getValue(), List.of("ЗАЛ"));
                row.createCell(testCell).setCellValue(testMark.doubleValue());
            }
            row.createCell(ratingCell).setCellValue(getAvgMark(entry.getValue(), getAllSubjects(studentMarks).values()).doubleValue());
            lastRow++;
        }

    }

    private void writeMarks(Sheet sheet, List<SubjectMark> marks, Row row) {
        for (SubjectMark subjectMark : marks) {
            for (int subjectColumn : subjectColumns) {
                if (sheet.getRow(0).getCell(subjectColumn).getStringCellValue().equals(subjectMark.getSubjectName()) &&
                        sheet.getRow(2).getCell(subjectColumn).getStringCellValue().equals(subjectMark.getType())) {
                    row.createCell(subjectColumn).setCellValue(Double.parseDouble(subjectMark.getMarkValue()));
                    break;
                }
            }
        }
    }

    private BigDecimal getAvgMark(List<SubjectMark> entry, Collection<String> types) {
        BigDecimal sum = entry.stream()
                .filter(mark -> types.contains(mark.getType()))
                .map(SubjectMark::getMarkValue)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(entry.stream()
                        .filter(mark -> types.contains(mark.getType()))
                        .count()),
                2, RoundingMode.HALF_UP);
    }

    private void writeHeader(Row row, Map<Student, List<SubjectMark>> studentMarks) {
        writeNumberCell(row.createCell(0), "№п/п");
        writeCenterCell(row.createCell(1), "П.І.Б.");
        writeCenterCell(row.createCell(2), "Група");
        writeCenterCell(row.createCell(3), "б/к");
        writeRatingCell(row.createCell(4), "Рейтинг");
        Map<String, String> allSubjects = getAllSubjects(studentMarks);
        int lastCell = writeSubjects(5, allSubjects, row, workbook.getSheetAt(0).createRow(2));
        subjectColumns = IntStream.rangeClosed(5, lastCell).toArray();
        if (allSubjects.containsValue("ІСП")) {
            writeExamsCell(row.createCell(lastCell), "Іспити");
        }
        if (allSubjects.containsValue("КР")) {
            writeCourseWork(row.createCell(++lastCell), "КР");
        }
        if (allSubjects.containsValue("ЗАЛ")) {
            writeTestsCell(row.createCell(++lastCell), "Заліки");
        }
    }

    private void writeTestsCell(Cell cell, String s) {
        writeMarkTypeCellOnHeader(cell, s);
        testCell = cell.getColumnIndex();
    }

    private void writeMarkTypeCellOnHeader(Cell cell, String s) {
        CellStyle ratingStile = workbook.createCellStyle();
        ratingStile.setVerticalAlignment(VerticalAlignment.CENTER);
        ratingStile.setAlignment(HorizontalAlignment.CENTER);
        ratingStile.setRotation((short) 90);
        cell.setCellStyle(ratingStile);
        cell.setCellValue(s);
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 2, cell.getColumnIndex(), cell.getColumnIndex());
        workbook.getSheetAt(0).addMergedRegion(mergedRegion);
    }

    private void writeCourseWork(Cell cell, String s) {
        writeMarkTypeCellOnHeader(cell, s);
        courseCell = cell.getColumnIndex();
    }

    private void writeExamsCell(Cell cell, String s) {
        writeMarkTypeCellOnHeader(cell, s);
        examCell = cell.getColumnIndex();
    }

    private int writeSubjects(int startCell, Map<String, String> allSubjects, Row row, Row typeRow) {
        CellStyle ratingStile = workbook.createCellStyle();
        ratingStile.setVerticalAlignment(VerticalAlignment.CENTER);
        ratingStile.setAlignment(HorizontalAlignment.CENTER);
        ratingStile.setRotation((short) 90);
        for (Map.Entry<String, String> entry : allSubjects.entrySet()) {
            Cell cell = row.createCell(startCell);
            cell.setCellValue(entry.getKey());
            cell.setCellStyle(ratingStile);
            Cell typeCell = typeRow.createCell(startCell);
            typeCell.setCellValue(entry.getValue());
            typeCell.setCellStyle(centerStyle());
            workbook.getSheetAt(0).setColumnWidth(startCell, 5 * 256);
            startCell++;
        }
        return startCell;
    }

    private Map<String, String> getAllSubjects(Map<Student, List<SubjectMark>> studentMarks) {
        return studentMarks.values().stream()
                .flatMap(Collection::stream)
                .filter(distinctByName(SubjectMark::getSubjectName))
                .sorted(Comparator.comparing(SubjectMark::getType))
                .collect(Collectors.toMap(SubjectMark::getSubjectName, SubjectMark::getType, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private static <T> Predicate<T> distinctByName(Function<T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void writeRatingCell(Cell cell, String s) {
        writeMarkTypeCellOnHeader(cell, s);
        ratingCell = cell.getColumnIndex();
    }

    private void writeCenterCell(Cell cell, String s) {
        cell.setCellStyle(centerStyle());
        cell.setCellValue(s);
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 2, cell.getColumnIndex(), cell.getColumnIndex());
        workbook.getSheetAt(0).addMergedRegion(mergedRegion);
    }

    private void writeNumberCell(Cell cell, String s) {
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setRotation((short) 255);
        numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numberStyle.setWrapText(true);
        cell.setCellStyle(numberStyle);
        cell.setCellValue(s);
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 2, cell.getColumnIndex(), cell.getColumnIndex());
        workbook.getSheetAt(0).addMergedRegion(mergedRegion);
    }

    private CellStyle centerStyle() {
        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        return centerStyle;
    }

}
