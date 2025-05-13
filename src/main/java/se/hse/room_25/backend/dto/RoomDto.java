package se.hse.room_25.backend.dto;

import lombok.Data;
import se.hse.room_25.backend.model.Cell;
import se.hse.room_25.backend.model.ControlData;
import se.hse.room_25.backend.model.Player;

import java.util.List;

@Data
public class RoomDto {
    private String id;
    private int numberOfPlayers;
    private List<Player> players;
    private Cell[][] board;
    private int totalTurns;
    private int currentTurn;
    private int currentPhase;
    private int currentPlayer;
    private String status;
    private ControlData controlData;
    private boolean keyFound;
}