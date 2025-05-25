package se.hse.room_25.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record LobbyCreateDto(@NotBlank
                             String gameMode,
                             @NotBlank
                             String difficulty,
                             @Min(2)
                             @Max(6)
                             int numberOfPlayers,
                             @NotBlank
                             String character) {
}
