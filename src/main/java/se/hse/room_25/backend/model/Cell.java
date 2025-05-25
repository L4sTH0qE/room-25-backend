package se.hse.room_25.backend.model;

import lombok.Data;

@Data
public class Cell {

    private CellType type;
    private boolean faceUp;
    private boolean vertical;

    public Cell(CellType type) {
        this.type = type;
        faceUp = false;
        vertical = false;
    }

    public void turnOver() {
        faceUp = !faceUp;
    }
}
