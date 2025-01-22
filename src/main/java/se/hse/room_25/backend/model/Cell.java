package se.hse.room_25.backend.model;

public class Cell {

    CellType type;
    boolean isFaceUp;

    public Cell(CellType type) {
        this.type = type;
        isFaceUp = false;
    }

    public void turnOver() {
        isFaceUp = !isFaceUp;
    }
}
