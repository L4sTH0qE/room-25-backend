package se.hse.room_25.backend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.hse.room_25.backend.dto.LobbyCreateDto;
import se.hse.room_25.backend.dto.LobbyJoinDto;
import se.hse.room_25.backend.dto.PlayerActionsDto;
import se.hse.room_25.backend.dto.RoomDto;
import se.hse.room_25.backend.entity.Client;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.model.*;
import se.hse.room_25.backend.repository.RoomRepository;

import java.lang.reflect.Type;
import java.util.*;

@Log4j2
@Service
public class RoomService {

    private AuthService authService;
    private RoomRepository roomRepository;

    private Map<UUID, Game> roomIdToGame = new HashMap<>();

    /// Initialise necessary Spring beans.
    @Autowired
    public void prepare(AuthService authService, RoomRepository roomRepository) {
        this.authService = authService;
        this.roomRepository = roomRepository;
    }

    /// Get all rooms.
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    /// Get room by id
    public Room getRoom(UUID roomId) throws Exception {

        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            throw new Exception("комната \"" + roomId + "\" не найдена");
        }
        return room.get();
    }

    /// Check room by id
    public Map<String, Object> checkRoom(UUID roomId, String token) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Optional<Room> roomOpt = roomRepository.findById(roomId);

        if (roomOpt.isEmpty()) {
            result.put("exists", false);
            result.put("status", "notFound");
            result.put("characters", Collections.emptyList());
            result.put("already_joined", false);
            return result;
        }

        Room room = roomOpt.get();
        result.put("exists", true);
        result.put("status", room.getStatus());
        try {
            result.put("characters", getAvailableCharacters(roomId));
        } catch (Exception e) {
            result.put("characters", Collections.emptyList());
        }

        String clientJson = authService.getClientByToken(token);
        Type clientType = new TypeToken<Client>() {
        }.getType();
        Client client = new Gson().fromJson(clientJson, clientType);
        String playersJson = room.getPlayers();
        Type playerListType = new TypeToken<List<Player>>() {
        }.getType();
        List<Player> players = new Gson().fromJson(playersJson, playerListType);

        for (Player player : players) {
            if (player.getClientId().equals(client.getId())) {
                result.put("already_joined", true);
                return result;
            }
        }
        result.put("already_joined", false);
        return result;
    }

    /// Get all characters
    public List<String> getAllCharacters() {

        List<String> characters = new ArrayList<>();
        for (PlayerCharacter character : PlayerCharacter.values()) {
            characters.add(character.getName());
        }
        return characters;
    }

    /// Get characters that are not already used in current game
    public List<String> getAvailableCharacters(UUID roomId) throws Exception {
        Room room = getRoom(roomId);
        String playersJson = room.getPlayers();

        Type playerListType = new TypeToken<List<Player>>() {
        }.getType();
        List<Player> players = new Gson().fromJson(playersJson, playerListType);

        List<String> characters = getAllCharacters();
        for (Player player : players) {
            characters.remove(player.getCharacter().getName());
        }
        return characters;
    }

    /// Returns room id.
    public String createRoom(LobbyCreateDto lobbyCreateDto, String token) throws Exception {
        String clientJson = authService.getClientByToken(token);
        Type clientType = new TypeToken<Client>() {
        }.getType();
        Client client = new Gson().fromJson(clientJson, clientType);

        Player player = new Player(client.getId(), client.getUsername(), PlayerCharacter.fromString(lobbyCreateDto.character()));
        List<Player> players = new ArrayList<>();
        players.add(player);
        String playersJson = new Gson().toJson(players);

        Game game = new Game(lobbyCreateDto.gameMode(), lobbyCreateDto.difficulty());
        String board = new Gson().toJson(game.getBoard());
        UUID roomId = UUID.randomUUID();
        roomIdToGame.put(roomId, game);

        ControlData controlData = new ControlData(0, ControlOrientation.NONE);
        String control = new Gson().toJson(controlData);

        Room room = new Room(lobbyCreateDto.numberOfPlayers(), playersJson, board, 12 - lobbyCreateDto.numberOfPlayers(), 1, 1, 0, control);
        room.setId(roomId);
        roomRepository.save(room);

        return roomId.toString();
    }

    public synchronized Room joinRoom(UUID roomId, LobbyJoinDto lobbyJoinDto, String token) throws Exception {
        List<String> characters = getAllCharacters();
        if (!characters.contains(lobbyJoinDto.character())) {
            throw new Exception("персонаж \"" + lobbyJoinDto.character() + "\" не доступен");
        }

        List<String> availableCharacters = getAvailableCharacters(roomId);
        if (!availableCharacters.contains(lobbyJoinDto.character())) {
            throw new Exception("персонаж уже выбран в комнате \"" + roomId + "\"");
        }

        String clientJson = authService.getClientByToken(token);
        Type clientType = new TypeToken<Client>() {
        }.getType();
        Client client = new Gson().fromJson(clientJson, clientType);

        Room room = getRoom(roomId);
        String playersJson = room.getPlayers();
        Type playerListType = new TypeToken<List<Player>>() {
        }.getType();
        List<Player> players = new Gson().fromJson(playersJson, playerListType);

        for (Player player : players) {
            if (player.getClientId().equals(client.getId())) {
                throw new Exception("вы уже присоединились к комнате \"" + roomId + "\"");
            }
        }
        Player newPlayer = new Player(client.getId(), client.getUsername(), PlayerCharacter.fromString(lobbyJoinDto.character()));
        players.add(newPlayer);
        room.setPlayers(new Gson().toJson(players));
        if (players.size() == room.getNumberOfPlayers()) {
            room.setStatus("started");
        }
        roomRepository.save(room);
        return room;
    }

    public RoomDto roomToDto(Room entity) {
        Gson gson = new Gson();
        RoomDto dto = new RoomDto();
        dto.setId(entity.getId().toString());
        dto.setNumberOfPlayers(entity.getNumberOfPlayers());
        dto.setTotalTurns(entity.getTotalTurns());
        dto.setCurrentTurn(entity.getCurrentTurn());
        dto.setCurrentPhase(entity.getCurrentPhase());
        dto.setCurrentPlayer(entity.getCurrentPlayer());
        dto.setStatus(entity.getStatus());

        Type playerListType = new TypeToken<List<Player>>() {
        }.getType();
        dto.setPlayers(gson.fromJson(entity.getPlayers(), playerListType));

        Type boardType = new TypeToken<Cell[][]>() {
        }.getType();
        dto.setBoard(gson.fromJson(entity.getBoard(), boardType));

        Type controlDataType = new TypeToken<ControlData>() {
        }.getType();
        dto.setControlData(gson.fromJson(entity.getControlData(), controlDataType));

        return dto;
    }

    public Room updatePlayerActions(UUID roomId, PlayerActionsDto playerActionsDto) throws Exception {
        Room room = getRoom(roomId);
        String playersJson = room.getPlayers();
        Type playerListType = new TypeToken<List<Player>>() {
        }.getType();
        List<Player> players = new Gson().fromJson(playersJson, playerListType);

        for (Player player : players) {
            if (player.getClientName().equals(playerActionsDto.player)) {
                player.getPlayerAction().programActions(PlayerActionType.fromTitle(playerActionsDto.getActions().getFirst()), PlayerActionType.fromTitle(playerActionsDto.getActions().get(1)));
                break;
            }
        }
        room.setPlayers(new Gson().toJson(players));

        for (Player player : players) {
            if (!player.getPlayerAction().isReady()) {
                roomRepository.save(room);
                return room;
            }
        }

        if (players.size() == room.getNumberOfPlayers()) {
            room.setCurrentPhase(2);
        }
        roomRepository.save(room);
        return room;
    }

    public Room updateGameStatus(UUID roomId, RoomDto roomDto) throws Exception {

        Room room = getRoom(roomId);

        room.setPlayers(new Gson().toJson(roomDto.getPlayers()));
        room.setBoard(new Gson().toJson(roomDto.getBoard()));
        room.setControlData(new Gson().toJson(roomDto.getControlData()));
        room.setStatus(roomDto.getStatus());

        int currentTurn = room.getCurrentTurn();
        int currentPhase = roomDto.getCurrentPhase();
        int currentPlayer = roomDto.getCurrentPlayer();
        currentPlayer++;
        if (currentPlayer >= roomDto.getNumberOfPlayers()) {
            currentPlayer %= roomDto.getNumberOfPlayers();
            currentPhase++;
            if (currentPhase > 3) {
                ControlData controlData = new ControlData(0, ControlOrientation.NONE);
                room.setControlData(new Gson().toJson(controlData));
                currentPhase = 1;
                currentTurn++;
                if (currentTurn > roomDto.getTotalTurns()) {
                    if (!Objects.equals(roomDto.getStatus(), "won")) {
                        room.setStatus("lost");
                    }
                }
            }
        }
        room.setCurrentTurn(currentTurn);
        room.setCurrentPhase(currentPhase);
        room.setCurrentPlayer(currentPlayer);

        roomRepository.save(room);
        return room;
    }
}
