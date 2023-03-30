package com.duop.analyzer.sheets.reader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

public class ExcelReader extends SpreadsheetsReader {
    private final Workbook workbook;

    public ExcelReader(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public String readCell(String spreadsheetId, String range) {
        Sheet sheet = workbook.getSheetAt(0);
        CellReference cellReference = new CellReference(range);
        Row row = sheet.getRow(cellReference.getRow());
        if (row != null) {
            Cell cell = row.getCell(cellReference.getCol());
            if (cell != null) {
                return getTypedValue(cell);
            }
        }
        return null;
    }

    private String getTypedValue(Cell cell) {
        CellType cellType = cell.getCellType();
        return switch (cellType) {
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue();
            case FORMULA -> cell.getCellFormula();
            case BLANK, ERROR, _NONE -> null;
        };
    }
}
