package se.hse.room_25.backend.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.hse.room_25.backend.dto.RoomDto;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.service.RoomService;

import java.security.Principal;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping()
public class GameController {
    private RoomService roomService;

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public void prepare(RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/room/{roomId}")
    public void getGameState(@DestinationVariable UUID roomId) {
        try {
            log.error("ABOBA");
            Room room = roomService.getRoom(roomId);
            log.error(room);
            RoomDto roomDto = roomService.roomToDto(room);
            log.error(roomDto);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, roomDto);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
