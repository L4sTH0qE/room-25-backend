package se.hse.room_25.backend.model;

import lombok.Getter;

@Getter
public enum PlayerCharacter {
    FRANK("frank"),
    KEVIN("kevin"),
    MAX("max"),
    EMMETT("emmett"),
    ALICE("alice"),
    JENNIFER("jennifer"),
    SARAH("sarah"),
    BRUCE("bruce");

    private final String name;

    PlayerCharacter(String name) {
        this.name = name;
    }

    public static PlayerCharacter fromString(String name) {
        for (PlayerCharacter character : PlayerCharacter.values()) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + name);
    }
}
