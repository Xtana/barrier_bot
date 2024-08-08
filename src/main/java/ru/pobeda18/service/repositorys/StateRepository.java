package ru.pobeda18.service.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pobeda18.db.tables.ClientState;

public interface StateRepository extends JpaRepository<ClientState, Long> {

    void deleteByClientTgId(Long clientTgId);
    ClientState findByClientTgId(Long clientTgId);
}
