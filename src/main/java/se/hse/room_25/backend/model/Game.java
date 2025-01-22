package se.hse.room_25.backend.model;

import lombok.Getter;
import se.hse.room_25.backend.service.RoomService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {

    // internal attributes - not sent over websocket
    private RoomService roomService;

    // game state attributes - sent over websocket
    private Cell[][] board;

    public Game(RoomService roomService) {
        this.roomService = roomService;
        initialiseBoard();
    }

    public void initialiseBoard() {
        List<Cell> cells = new ArrayList<>();

        // Rooms to be added at the second phase of generation/
        cells.add(new Cell(CellType.ROOM_25));
        cells.add(new Cell(CellType.KEY_ROOM));
        cells.add(new Cell(CellType.CONTROL_ROOM));
        cells.add(new Cell(CellType.OBSERVATION_ROOM));

        cells.add(new Cell(CellType.EMPTY_ROOM));
        cells.add(new Cell(CellType.DARK_ROOM));
        cells.add(new Cell(CellType.DARK_ROOM));
        cells.add(new Cell(CellType.FREEZER_ROOM));
        cells.add(new Cell(CellType.FREEZER_ROOM));
        cells.add(new Cell(CellType.WHIRLPOOL_ROOM));
        cells.add(new Cell(CellType.WHIRLPOOL_ROOM));
        cells.add(new Cell(CellType.CORRIDOR_ROOM));
        cells.add(new Cell(CellType.CORRIDOR_ROOM));
        cells.add(new Cell(CellType.JAIL_ROOM));
        cells.add(new Cell(CellType.JAIL_ROOM));
        cells.add(new Cell(CellType.ACID_ROOM));
        cells.add(new Cell(CellType.ACID_ROOM));
        cells.add(new Cell(CellType.TRAP_ROOM));
        cells.add(new Cell(CellType.TRAP_ROOM));
        cells.add(new Cell(CellType.TORTURE_ROOM));
        cells.add(new Cell(CellType.TORTURE_ROOM));
        cells.add(new Cell(CellType.FLOODED_ROOM));
        cells.add(new Cell(CellType.FLOODED_ROOM));
        cells.add(new Cell(CellType.DEATH_ROOM));

        SecureRandom secureRandom = new SecureRandom();
        board = new Cell[5][5];
        board[2][2] = new Cell(CellType.CENTRAL_ROOM);

        // First phase.
        int index = secureRandom.nextInt(4, cells.size());
        board[0][2] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[1][1] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[1][2] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[1][3] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[2][0] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[2][1] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[2][3] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[2][4] = cells.get(index);
        cells.remove(index);
        board[3][1] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[3][2] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[3][3] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(4, cells.size());
        board[4][2] = cells.get(index);
        cells.remove(index);

        // Second phase;
        index = secureRandom.nextInt(cells.size());
        board[0][0] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[0][1] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[0][3] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[0][4] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[1][0] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[1][4] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[3][0] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[3][4] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[4][0] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[4][1] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[4][3] = cells.get(index);
        cells.remove(index);
        index = secureRandom.nextInt(cells.size());
        board[4][4] = cells.get(index);
        cells.remove(index);
    }
}
