package ru.pobeda18.db.tables;


import jakarta.persistence.*;
import lombok.Data;
import ru.pobeda18.db.tables.states.UserState;

@Data
@Entity
@Table(name = "ClientState")
public class ClientState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_tg_id", unique = true)
    private Long clientTgId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state", columnDefinition = "enum ('HAS_CAR_COMMAND')")
    private UserState userState;
}
