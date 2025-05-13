package se.hse.room_25.backend.model;

import lombok.Data;

@Data
public class ControlData {

    private int row;
    private ControlOrientation orientation;

    public ControlData(int row, ControlOrientation orientation) {
        this.row = row;
        this.orientation = orientation;
    }
}
