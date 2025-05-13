package se.hse.room_25.backend.model;

import lombok.Data;

@Data
public class Cell {

    private CellType type;
    private boolean faceUp;

    public Cell(CellType type) {
        this.type = type;
        faceUp = false;
    }

    public void turnOver() {
        faceUp = !faceUp;
    }
}
