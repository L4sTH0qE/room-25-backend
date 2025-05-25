package se.hse.room_25.backend.model;

import lombok.Getter;

@Getter
public enum PlayerActionType {
    LOOK("LOOK"),
    MOVE("MOVE"),
    PUSH("PUSH"),
    CONTROL("CONTROL"),
    NONE("NONE");

    private final String title;

    PlayerActionType(String title) {
        this.title = title;
    }

    public static PlayerActionType fromTitle(String title) {
        for (PlayerActionType actionType : PlayerActionType.values()) {
            if (actionType.getTitle().equals(title)) {
                return actionType;
            }
        }
        return null;
    }
}
