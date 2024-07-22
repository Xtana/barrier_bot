package ru.pobeda18.client;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SheetsClient {
    private Sheets sheetsService;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("${google.sheets.name}")
    private String applicationName;
    @Value("${google.sheets.id}")
    private String sheetId;
    @Value("${google.sheets.range}")
    private  String range;

    private Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetsClient.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(in));
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
                clientSecrets, scopes
        ).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");

        return credential;
    }

    private Sheets getSheetsService() throws GeneralSecurityException, IOException {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    public List<List<Object>> getDataList() throws IOException, GeneralSecurityException {
        log.info("Performing client call to googleApi to get data from sheets");

        List<List<Object>> sheetDataList;
        try {
            sheetsService = getSheetsService();
            sheetDataList = sheetsService.spreadsheets().values()
                    .get(sheetId, range)
                    .execute().getValues();

        } catch (IOException | GeneralSecurityException e) {
            log.error("Error while getting data from google sheets", e);
            throw e;
        }
        return sheetDataList;
    }

    public Map<String, UserSheetData> getDataMap() throws GeneralSecurityException, IOException {
        List<List<Object>> sheetDataList = null;

        synchronized (this) {
            sheetDataList = getDataList();
        }

        Map<String, UserSheetData> sheetDataMap = new HashMap<>();

        for (int i = 1; i < sheetDataList.size(); i++) {
            UserSheetData userData = new UserSheetData(
                    sheetDataList.get(i).get(0).toString(),
                    sheetDataList.get(i).get(1).toString(),
                    sheetDataList.get(i).get(2).toString()
            );
            sheetDataMap.put(userData.getNumber(), userData);
        }
        return sheetDataMap;
    }
}
