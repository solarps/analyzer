package com.duop.analyzer.sheets.reader;


import java.io.IOException;

public abstract class SpreadsheetsReader {

    public abstract String readCell(String spreadsheetId, String range) throws IOException;
}
