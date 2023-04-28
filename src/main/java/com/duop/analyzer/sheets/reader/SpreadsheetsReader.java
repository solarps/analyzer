package com.duop.analyzer.sheets.reader;

import java.io.IOException;
import java.util.List;

public interface SpreadsheetsReader {
    String readCell(String spreadsheetId, String cellRange) throws IOException;

    String readMergedCell(String spreadsheetId, String cellRange) throws IOException;

    List<List<String>> readInRangeExcludedFields(String spreadsheetId, String range, int... fields)
            throws IOException;

    List<List<String>> readInRange(String spreadsheetId, String range) throws IOException;

    String getRangeForTable(String spreadsheetId, String startCell, String endColumn)
            throws IOException;
}
