package ru.pobeda18.service.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pobeda18.db.tables.TempCarNumber;

public interface TempCarNumberRepository extends JpaRepository<TempCarNumber, Long> {

    void deleteByClientTgId(Long clientTgId);
    TempCarNumber findByClientTgId(Long clientTgId);
}
