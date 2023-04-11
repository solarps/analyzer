package com.duop.analyzer.sheets.reader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelReader extends SpreadsheetsReader {
    private final Workbook workbook;

    public ExcelReader(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public String readCell(String spreadsheetId, String cellRange) {
        Sheet sheet = workbook.getSheetAt(0);
        CellReference cellReference = new CellReference(cellRange);
        Row row = sheet.getRow(cellReference.getRow());
        if (row != null) {
            Cell cell = row.getCell(cellReference.getCol());
            if (cell != null) {
                return getTypedValue(cell);
            }
        }
        return null;
    }

    @Override
    public String readMergedCell(String spreadsheetId, String cellRange) {
        CellRangeAddress cellRangeAddress = CellRangeAddress.valueOf(cellRange);
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(cellRangeAddress.getFirstRow());
        Cell cell = row.getCell(cellRangeAddress.getFirstColumn());
        return cell.getStringCellValue();
    }

    @Override
    public List<List<String>> readInRangeExcludedFields(String id, String range, int... fields) {
        Sheet sheet = workbook.getSheetAt(0);
        CellRangeAddress cellRangeAddress = CellRangeAddress.valueOf(range);
        List<List<String>> result = new ArrayList<>();
        for (Row row : sheet) {
            int rowNum = row.getRowNum();
            if (rowNum < cellRangeAddress.getFirstRow() || rowNum > cellRangeAddress.getLastRow()) {
                continue;
            }

            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                int colNum = cell.getColumnIndex();
                if (Arrays.stream(fields).anyMatch(f -> f == colNum) ||
                        colNum > cellRangeAddress.getLastColumn() ||
                        colNum < cellRangeAddress.getFirstColumn()) {
                    continue;
                }

                rowData.add(getTypedValue(cell));
            }

            result.add(rowData);
        }

        return result;
    }

    @Override
    public String getRangeForTable(String spreadsheetId, String startCell, String endColumn) {
        Sheet sheet = workbook.getSheetAt(0);
        int lastRow = sheet.getLastRowNum();
        String endCell = endColumn + lastRow;
        int currentRow = sheet.getRow(Integer.parseInt(startCell.substring(1))).getRowNum() + 1;

        while (currentRow <= lastRow) {
            Row row = sheet.getRow(currentRow);
            Cell cell = row.getCell(CellReference.convertColStringToIndex(endColumn));
            if (getTypedValue(cell) == null) {
                break;
            }
            currentRow++;
        }

        String range = startCell + ":" + endCell;
        if (currentRow > sheet.getRow(Integer.parseInt(startCell.substring(1))).getRowNum() + 1) {
            range = startCell + ":" + endColumn + (currentRow);
        }
        return range;
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
