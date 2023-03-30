package com.duop.analyzer.sheets.reader;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.List;

public class GoogleSheetsReader extends SpreadsheetsReader {

    private final Sheets service;

    public GoogleSheetsReader(Sheets service) {
        this.service = service;
    }

    @Override
    public String readCell(String spreadsheetId, String range) throws IOException {
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        if (values != null && !values.isEmpty()) {
            List<Object> row = values.get(0);
            if (row != null && !row.isEmpty()) {
                return row.get(0).toString();
            }
        }
        return null;
    }
}
