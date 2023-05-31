package com.duop.analyzer.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.drive.DriveScopes;
import lombok.SneakyThrows;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class GoogleAuthorizeUtil {
    /**
     * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
     * your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential credential;

    private GoogleAuthorizeUtil() {
    }

    @SneakyThrows
    public static Credential getCredential() {
        if (credential != null) return credential;
        // Load client secrets.
        InputStream in = GoogleAuthorizeUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        credential = GoogleCredential.fromStream(in).createScoped(SCOPES);
        return credential;
    }


}
