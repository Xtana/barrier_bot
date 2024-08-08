package ru.pobeda18.db.tables;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TempCarNumbers")
public class TempCarNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_tg_id", unique = true)
    private Long clientTgId;

    @Column(name = "temp_car_number")
    private String tempCarNumber;
}
