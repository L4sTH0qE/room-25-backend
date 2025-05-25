package se.hse.room_25.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlayerActionsDto {
    public String player;
    public List<String> actions;
}
