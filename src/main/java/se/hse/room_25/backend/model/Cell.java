package se.hse.room_25.backend.model;

import lombok.Data;

@Data
public class Cell {

    private CellType type;
    private boolean isFaceUp;

    public Cell(CellType type) {
        this.type = type;
        isFaceUp = false;
    }

    public void turnOver() {
        isFaceUp = !isFaceUp;
    }
}
