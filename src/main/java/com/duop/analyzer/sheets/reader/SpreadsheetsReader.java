package com.duop.analyzer.sheets.reader;


import java.io.IOException;
import java.util.List;

public abstract class SpreadsheetsReader {

    public abstract String readCell(String spreadsheetId, String cellRange) throws IOException;

    public abstract String readMergedCell(String spreadsheetId, String cellRange) throws IOException;

    public abstract List<List<String>> readInRangeExcludedFields(String spreadsheetId, String range, int... fields) throws IOException;

    public abstract String getRangeForTable(String spreadsheetId,
                                            String startCell,
                                            String endColumn) throws IOException;
}
