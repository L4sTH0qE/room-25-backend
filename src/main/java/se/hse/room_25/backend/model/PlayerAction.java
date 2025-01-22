package se.hse.room_25.backend.model;

import lombok.Data;

@Data
public class PlayerAction {

    private PlayerActionType firstAction;
    private PlayerActionType secondAction;
    private PlayerActionType thirdAction;

    public PlayerAction() {
    }

    public void programActions(PlayerActionType firstAction, PlayerActionType secondAction, PlayerActionType thirdAction) {
        this.firstAction = firstAction;
        this.secondAction = secondAction;
        this.thirdAction = thirdAction;
    }
}
