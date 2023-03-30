package com.duop.analyzer.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class GoogleServicesUtil {

    private GoogleServicesUtil() {
    }

    private static final String APPLICATION_NAME = "Google sheets analyzer";
    private static final Credential credential = GoogleAuthorizeUtil.getCredential();

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

    }

    public static Drive getDriverService() throws GeneralSecurityException, IOException {
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
