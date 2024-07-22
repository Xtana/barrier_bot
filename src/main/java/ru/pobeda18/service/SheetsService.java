package ru.pobeda18.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pobeda18.client.SheetsClient;
import ru.pobeda18.client.UserSheetData;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class SheetsService {

    private final AtomicReference<Map<String, UserSheetData>> sheetsData = new AtomicReference<>();
    private final SheetsClient client;

    public SheetsService(SheetsClient client) {
        this.client = client;
    }

    public boolean isContainNumber(String number) throws GeneralSecurityException, IOException {
        updateData();
        return sheetsData.get().containsKey(number);
    }

    public UserSheetData getInfoByNumber(String number) throws GeneralSecurityException, IOException {
        updateData();
        return sheetsData.get().get(number);
    }

    private void updateData() throws GeneralSecurityException, IOException {
        sheetsData.set(client.getDataMap());
    }

}
