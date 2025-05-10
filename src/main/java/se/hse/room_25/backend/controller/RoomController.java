package se.hse.room_25.backend.controller;

import com.google.gson.Gson;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import se.hse.room_25.backend.dto.LobbyCreateDto;
import se.hse.room_25.backend.dto.LobbyJoinDto;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.service.RoomService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/room")
public class RoomController {

    private RoomService roomService;

    @Autowired
    public void prepare(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/characters")
    public ResponseEntity<String> getAllCharacters() {
        try {
            List<String> characters = roomService.getAllCharacters();
            return ResponseEntity.ok(new Gson().toJson(characters));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @PostMapping()
    public ResponseEntity<String> createRoom(@RequestHeader("Authorization") String authHeader, @RequestBody @Valid LobbyCreateDto lobbyCreateDto, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"error\":\"неверные игровые данные\"}");
        }

        try {
            String token = authHeader.substring(7);
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"id\":\"" + roomService.createRoom(lobbyCreateDto, token) + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @GetMapping("/{roomId}/characters")
    public ResponseEntity<String> getAvailableCharacters(@PathVariable UUID roomId) {
        try {
            List<String> characters = roomService.getAvailableCharacters(roomId);
            return ResponseEntity.ok(new Gson().toJson(characters));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @GetMapping("/check/{roomId}")
    public ResponseEntity<String> checkRoom(@PathVariable UUID roomId, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Map<String, Object> result = roomService.checkRoom(roomId, token);
            return ResponseEntity.ok(new Gson().toJson(result));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @GetMapping()
    public ResponseEntity<String> getRooms() {
        try {
            List<Room> rooms = roomService.getRooms();
            return ResponseEntity.ok(new Gson().toJson(rooms));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @PostMapping("/join/{roomId}")
    public ResponseEntity<String> joinRoomByToken(@PathVariable UUID roomId, @RequestHeader("Authorization") String authHeader, @RequestBody LobbyJoinDto lobbyJoinDto, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"error\":\"неверные игровые данные\"}");
        }
        try {
            String token = authHeader.substring(7);
            Room room = roomService.joinRoom(roomId, lobbyJoinDto, token);
            return ResponseEntity.ok("{\"id\":\"" + room.getId().toString() + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}
