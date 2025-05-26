package se.hse.room_25.backend.game;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.hse.room_25.backend.dto.*;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.model.Cell;
import se.hse.room_25.backend.model.CellType;
import se.hse.room_25.backend.model.PlayerStatus;
import se.hse.room_25.backend.service.AuthService;
import se.hse.room_25.backend.service.RoomService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GameLogicTest {

    @Autowired
    RoomService roomService;
    @Autowired
    AuthService authService;

    @Test
    void testMoveActionFlipsCellAndMovesPlayer() throws Exception {
        // --- Arrange: Создаём комнату и игрока
        String token = authService.login(new AuthDto("playerA", "SuperPwd1!"));
        LobbyCreateDto lobbyDto = new LobbyCreateDto("COOP", "EASY", 2, "FRANK");
        String roomId = roomService.createRoom(lobbyDto, token);

        Room room = roomService.getRoom(UUID.fromString(roomId));
        RoomDto dto = roomService.roomToDto(room);

        // --- Act: playerA планирует MOVE
        PlayerActionsDto plan = new PlayerActionsDto();

        roomService.updatePlayerActions(UUID.fromString(roomId), plan);

        // Сдвигаем фазу, эмулируем выполнение action
        dto.getPlayers().getFirst().setCoordX(2);
        dto.getPlayers().getFirst().setCoordY(3);
        dto.getBoard()[2][3].setFaceUp(true);

        roomService.updateGameStatus(UUID.fromString(roomId), dto);

        // --- Assert: игрок теперь на [2,3], клетка открыта
        RoomDto result = roomService.roomToDto(roomService.getRoom(UUID.fromString(roomId)));
        assertEquals(2, result.getPlayers().getFirst().getCoordX());
        assertEquals(3, result.getPlayers().getFirst().getCoordY());
        assertTrue(result.getBoard()[2][3].isFaceUp());
    }

    @Test
    void testPushActionAndTrapDeath() throws Exception {
        String tokenA = authService.login(new AuthDto("pA", "PssWord@1"));
        String tokenB = authService.login(new AuthDto("pB", "PssWord@2"));

        // 1. Игрок A создаёт комнату и заходит
        LobbyCreateDto lobbyDto = new LobbyCreateDto("COOP", "EASY", 2, "FRANK");
        String roomId = roomService.createRoom(lobbyDto, tokenA);

        // 2. Игрок B присоединяется
        LobbyJoinDto joinDto = new LobbyJoinDto("JENNIFER");
        roomService.joinRoom(UUID.fromString(roomId), joinDto, tokenB);

        Room room = roomService.getRoom(UUID.fromString(roomId));
        RoomDto dto = roomService.roomToDto(room);

        // Ставим обоих на клетку [2,2] (центральная)
        dto.getPlayers().forEach(p -> { p.setCoordX(2); p.setCoordY(2); });
        // Ставим рядом TRAP_ROOM, имитируем PUSH
        dto.getBoard()[2][3] = new Cell(CellType.TRAP_ROOM);

        // Игрок FRANK толкает JENNIFER в TRAP_ROOM
        dto.getPlayers().get(1).setCoordX(2);
        dto.getPlayers().get(1).setCoordY(3);

        // Эмулируем применение эффекта TRAP_ROOM через логику
        roomService.updateGameStatus(UUID.fromString(roomId), dto);
        RoomDto result = roomService.roomToDto(roomService.getRoom(UUID.fromString(roomId)));

        // --- ASSERT: игрок JENNIFER имеет статус TRAPPED
        assertEquals(PlayerStatus.TRAPPED, result.getPlayers().get(1).getStatus());
    }

    @Test
    void testWinKeyRoomAndEscape() throws Exception {
        String token = authService.login(new AuthDto("aaa", "Password!1"));
        LobbyCreateDto dto = new LobbyCreateDto("COOP", "EASY", 1, "FRANK");
        String roomId = roomService.createRoom(dto, token);
        Room room = roomService.getRoom(UUID.fromString(roomId));

        RoomDto roomDto = roomService.roomToDto(room);
        // Активируем KEY_ROOM
        roomDto.getPlayers().getFirst().setCoordX(0);
        roomDto.getPlayers().getFirst().setCoordY(0);
        roomDto.getBoard()[0][0] = new Cell(CellType.KEY_ROOM);
        roomDto.setKeyFound(true);

        // Перемещаем в ROOM_25 (победа)
        roomDto.getPlayers().getFirst().setCoordX(4);
        roomDto.getPlayers().getFirst().setCoordY(4);
        roomDto.getBoard()[4][4] = new Cell(CellType.ROOM_25);

        roomService.updateGameStatus(UUID.fromString(roomId), roomDto);
        Room resultRoom = roomService.getRoom(UUID.fromString(roomId));
        // --- ASSERT
        assertEquals("won", resultRoom.getStatus());
    }

    @Test
    void testDeathRoomDefeat() throws Exception {
        String token = authService.login(new AuthDto("zzz", "PasswordZZ1!"));
        LobbyCreateDto dto = new LobbyCreateDto("COOP", "MEDIUM", 1, "FRANK");
        String roomId = roomService.createRoom(dto, token);

        Room room = roomService.getRoom(UUID.fromString(roomId));
        RoomDto roomDto = roomService.roomToDto(room);
        roomDto.getPlayers().getFirst().setCoordX(2);
        roomDto.getPlayers().getFirst().setCoordY(1);
        roomDto.getBoard()[2][1] = new Cell(CellType.DEATH_ROOM);

        // После перемещения в DEATH_ROOM
        roomService.updateGameStatus(UUID.fromString(roomId), roomDto);

        RoomDto r = roomService.roomToDto(roomService.getRoom(UUID.fromString(roomId)));

        // ASSERT: статус DEAD и room.status = "lost"
        assertEquals(PlayerStatus.DEAD, r.getPlayers().getFirst().getStatus());
        assertEquals("lost", r.getStatus());
    }
}
