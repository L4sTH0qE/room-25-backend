package se.hse.room_25.backend.controller;

import com.google.gson.Gson;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import se.hse.room_25.backend.dto.GameDto;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.service.RoomService;

import java.util.List;
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
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }

    @PostMapping()
    public ResponseEntity<String> createRoom(@RequestHeader("Authorization") String authHeader, @RequestBody @Valid GameDto gameDto, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"error\":\"invalid game data\"}");
        }

        try {
            String token = authHeader.substring(7);
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"id\":\"" + roomService.createRoom(gameDto, token) + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }

    @GetMapping("/{roomId}/characters")
    public ResponseEntity<String> getAvailableCharacters(@PathVariable UUID roomId) {
        try {
            List<String> characters = roomService.getAvailableCharacters(roomId);
            return ResponseEntity.ok(new Gson().toJson(characters));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<String> getRoom(@PathVariable UUID roomId) {
        try {
            Room room = roomService.getRoom(roomId);
            return ResponseEntity.ok(new Gson().toJson(room));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }

    @GetMapping()
    public ResponseEntity<String> getRooms() {
        try {
            List<Room> rooms = roomService.getRooms();
            return ResponseEntity.ok(new Gson().toJson(rooms));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }

    @MessageMapping("/room/join/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ResponseEntity<String> joinRoomByToken(@DestinationVariable UUID roomId, @RequestHeader("Authorization") String authHeader, @RequestBody String character) {
        try {
            String token = authHeader.substring(7);
            return ResponseEntity.ok("{\"id\":\"" + roomService.joinRoom(roomId, character, token) + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }
}
