package google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class GoogleSheetsCredential {
    public static Credential credential;

    public static Credential getCredential() throws IOException {
        if (credential == null) {
            InputStream is = GoogleSheetsCredential.class
                    .getResourceAsStream("/credentials.json");
            credential = GoogleCredential.fromStream(is)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        }
        return credential;
    }
}