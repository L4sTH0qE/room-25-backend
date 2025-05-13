package se.hse.room_25.backend.model;

import lombok.Data;

@Data
public class PlayerAction {

    private PlayerActionType firstAction;
    private PlayerActionType secondAction;
    private boolean isReady;

    public PlayerAction() {
        isReady = false;
    }

    public void programActions(PlayerActionType firstAction, PlayerActionType secondAction) {
        this.firstAction = firstAction;
        this.secondAction = secondAction;
        isReady = true;
    }
}
