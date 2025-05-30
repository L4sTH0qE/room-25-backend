package se.hse.room_25.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Data
@Entity
@Table(name = "room", schema = "public")
public class Room {

    @Id
    UUID id;

    int numberOfPlayers;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String players;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String board;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String controlData;

    int totalTurns;

    int currentTurn;

    int currentPhase;

    int currentPlayer;

    String status = "waiting";

    boolean keyFound = false;

    String waitingEffect = "";

    public Room() {
    }

    public Room(int numberOfPlayers, String players, String board, int totalTurns, int currentTurn, int currentPhase, int currentPlayer, String controlData) {
        this.numberOfPlayers = numberOfPlayers;
        this.players = players;
        this.board = board;
        this.totalTurns = totalTurns;
        this.currentTurn = currentTurn;
        this.currentPhase = currentPhase;
        this.currentPlayer = currentPlayer;
        this.controlData = controlData;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
