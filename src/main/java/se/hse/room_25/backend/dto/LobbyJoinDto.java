package se.hse.room_25.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LobbyJoinDto(@NotBlank
                           String character) {
}