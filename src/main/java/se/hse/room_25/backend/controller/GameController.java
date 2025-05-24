package se.hse.room_25.backend.controller;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.hse.room_25.backend.dto.PlayerActionsDto;
import se.hse.room_25.backend.dto.RoomDto;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.service.RoomService;

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
            Room room = roomService.getRoom(roomId);
            RoomDto roomDto = roomService.roomToDto(room);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, roomDto);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @MessageMapping("/room/{roomId}/preparation")
    public void handleSubmitActions(@DestinationVariable UUID roomId, @Payload PlayerActionsDto dto) {
        try {
            Room room = roomService.updatePlayerActions(roomId, dto);
            if (room.getCurrentPhase() == 2) {
                RoomDto roomDto = roomService.roomToDto(room);
                messagingTemplate.convertAndSend("/topic/room/" + roomId, roomDto);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @MessageMapping("/room/{roomId}/action")
    public void handleMakeActions(@DestinationVariable UUID roomId, @Payload String jsonRoomDto) {
        try {
            RoomDto roomDto = new Gson().fromJson(jsonRoomDto, RoomDto.class);
            Room room = roomService.updateGameStatus(roomId, roomDto);
            RoomDto dto = roomService.roomToDto(room);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, dto);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @MessageMapping("/room/{roomId}/push-event")
    public void handlePushEffect(@DestinationVariable UUID roomId, @Payload String jsonRoomDto) {
        try {
            RoomDto roomDto = new Gson().fromJson(jsonRoomDto, RoomDto.class);
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/push-effect", roomDto);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
