package ru.pobeda18.service;

import ru.pobeda18.db.tables.ClientState;
import ru.pobeda18.db.tables.TempCarNumber;

public interface ClientService {
    ClientState saveClientState(ClientState clientState);
    ClientState findStateByClientTgId(Long clientTgId);
    void deleteClientState(Long clientTgId);
    TempCarNumber saveTempCarNumber(TempCarNumber tempCarNumber);
    void deleteTempCarNumber(Long clientTgId);
    TempCarNumber findTempCarNumberByClientTgId(Long clientTgId);

}
