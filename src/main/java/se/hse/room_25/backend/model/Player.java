package se.hse.room_25.backend.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Player {

    private UUID clientId;
    private String clientName;
    private int coordX;
    private int coordY;
    private CellType currentCell;
    private PlayerCharacter character;
    private PlayerAction playerAction;
    private PlayerStatus status;
    private boolean isAlive;

    public Player(UUID clientId, String clientName, PlayerCharacter character) {
        this.clientId = clientId;
        this.clientName = clientName;
        coordX = 2;
        coordY = 2;
        currentCell = CellType.CENTRAL_ROOM;
        this.character = character;
        playerAction = new PlayerAction();
        status = PlayerStatus.NORMAL;
        isAlive = true;
    }
}
