package se.hse.room_25.backend.room;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.hse.room_25.backend.dto.AuthDto;
import se.hse.room_25.backend.dto.LobbyCreateDto;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.service.AuthService;
import se.hse.room_25.backend.service.RoomService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoomServiceTest {

    @Autowired
    private RoomService roomService;
    @Autowired
    private AuthService authService;

    @Test
    void createRoomTest() throws Exception {
        String token = authService.login(new AuthDto("playerX", "Password99!"));
        LobbyCreateDto dto = new LobbyCreateDto("COOP", "EASY", 2, "FRANK");
        String roomId = roomService.createRoom(dto, token);
        assertNotNull(roomId);

        Room room = roomService.getRoom(UUID.fromString(roomId));
        assertEquals("waiting", room.getStatus());
        assertTrue(room.getPlayers().contains("FRANK"));
    }
}
