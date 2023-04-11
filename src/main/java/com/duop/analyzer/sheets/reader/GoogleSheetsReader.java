package com.duop.analyzer.sheets.reader;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSheetsReader extends SpreadsheetsReader {

    private final Sheets service;

    public GoogleSheetsReader(Sheets service) {
        this.service = service;
    }

    @Override
    public String readCell(String spreadsheetId, String cellRange) throws IOException {
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, cellRange + ":" + cellRange).execute();
        return getCellValue(response);
    }

    @Override
    public String readMergedCell(String spreadsheetId, String cellRange) throws IOException {
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, cellRange).execute();
        return getCellValue(response);
    }

    private String getCellValue(ValueRange response) {
        List<List<Object>> values = response.getValues();
        if (values != null && !values.isEmpty()) {
            List<Object> row = values.get(0);
            if (row != null && !row.isEmpty()) {
                return row.get(0).toString();
            }
        }
        return null;
    }

    @Override
    public List<List<String>> readInRangeExcludedFields(String spreadsheetId, String range, int... fields) throws IOException {
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        List<List<String>> result = new ArrayList<>();

        if (values == null || values.isEmpty()) {
            return result;
        }

        for (List<Object> row : values) {
            List<String> newRow = new ArrayList<>();
            for (int i = 0; i < row.size(); i++) {
                int column = i;
                if (Arrays.stream(fields).noneMatch(x -> x - 1 == column)) {
                    Object cell = row.get(i);
                    newRow.add(cell == null ? "" : cell.toString());
                }
            }
            result.add(newRow);
        }

        return result;
    }

    @Override
    public String getRangeForTable(String spreadsheetId, String startCell, String endColumn) throws IOException {
        String range = startCell + ":" + endColumn;
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        int lastRow = values.size();
        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row.isEmpty()) {
                lastRow = i;
                break;
            }
        }
        String endCell = CellReference.convertNumToColString(CellReference.convertColStringToIndex(endColumn)) + (lastRow + Integer.parseInt(startCell.substring(1)) - 1);
        return startCell + ":" + endCell;
    }
}
