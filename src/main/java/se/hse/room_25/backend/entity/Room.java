package se.hse.room_25.backend.entity;

import jakarta.persistence.*;
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

    String players;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String board;

    int totalTurns;

    int currentTurn;

    int currentPhase;

    int currentPlayer;

    String status = "waiting";

    public Room() {
    }

    public Room(int numberOfPlayers, String players, String board, int totalTurns, int currentTurn, int currentPhase, int currentPlayer) {
        this.numberOfPlayers = numberOfPlayers;
        this.players = players;
        this.board = board;
        this.totalTurns = totalTurns;
        this.currentTurn = currentTurn;
        this.currentPhase = currentPhase;
        this.currentPlayer = currentPlayer;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
