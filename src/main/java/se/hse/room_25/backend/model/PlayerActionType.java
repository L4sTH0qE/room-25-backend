package se.hse.room_25.backend.model;

import lombok.Getter;

@Getter
public enum PlayerActionType {
    LOOK("look"),
    MOVE("move"),
    PUSH("push"),
    CONTROL("control");

    private final String title;

    PlayerActionType(String title) {
        this.title = title;
    }
}
