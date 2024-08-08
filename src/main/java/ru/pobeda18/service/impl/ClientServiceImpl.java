package ru.pobeda18.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pobeda18.db.tables.ClientState;
import ru.pobeda18.db.tables.TempCarNumber;
import ru.pobeda18.service.ClientService;
import ru.pobeda18.service.repositorys.StateRepository;
import ru.pobeda18.service.repositorys.TempCarNumberRepository;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final StateRepository stateRepository;
    private final TempCarNumberRepository tempCarNumberRepository;

    @Override
    public ClientState saveClientState(ClientState clientState) {
        return stateRepository.save(clientState);
    }

    @Override
    public ClientState findStateByClientTgId(Long clientTgId) {
        return stateRepository.findByClientTgId(clientTgId);
    }

    @Override
    @Transactional
    public void deleteClientState(Long clientTgId) {
        stateRepository.deleteByClientTgId(clientTgId);
    }

    @Override
    public TempCarNumber saveTempCarNumber(TempCarNumber tempCarNumber) {
        return tempCarNumberRepository.save(tempCarNumber);
    }

    @Override
    @Transactional
    public void deleteTempCarNumber(Long clientTgId) {
        tempCarNumberRepository.deleteByClientTgId(clientTgId);
    }

    @Override
    public TempCarNumber findTempCarNumberByClientTgId(Long clientTgId) {
        return tempCarNumberRepository.findByClientTgId(clientTgId);
    }

}
