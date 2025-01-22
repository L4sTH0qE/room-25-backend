package se.hse.room_25.backend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.hse.room_25.backend.dto.GameDto;
import se.hse.room_25.backend.entity.Client;
import se.hse.room_25.backend.entity.Room;
import se.hse.room_25.backend.model.Game;
import se.hse.room_25.backend.model.Player;
import se.hse.room_25.backend.model.PlayerCharacter;
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
            throw new Exception("room with id \"" + roomId + "\" is not found");
        }
        return room.get();
    }

    /// Get all characters
    public List<String> getAllCharacters() {

        List<String> characters = new ArrayList<>();
        for (PlayerCharacter character: PlayerCharacter.values()) {
            characters.add(character.getName());
        }
        return characters;
    }

    /// Get characters that are not already used in current game
    public List<String> getAvailableCharacters(UUID roomId) throws Exception {
        Room room = getRoom(roomId);
        String playersJson = room.getPlayers();

        Type playerListType = new TypeToken<List<Player>>(){}.getType();
        List<Player> players = new Gson().fromJson(playersJson, playerListType);

        List<String> characters = getAllCharacters();
        for (Player player: players) {
            characters.remove(player.getCharacter().getName());
        }
        return characters;
    }

    /// Returns room id.
    public String createRoom(GameDto gameDto, String token) throws Exception {
        String clientJson = authService.getClientByToken(token);
        Type clientType = new TypeToken<Client>(){}.getType();
        Client client =  new Gson().fromJson(clientJson, clientType);

        Player player = new Player(client.getId(), PlayerCharacter.fromString(gameDto.character()));
        List<Player> players = new ArrayList<>();
        players.add(player);
        String playersJson = new Gson().toJson(players);

        Game game = new Game(this);
        String board = new Gson().toJson(game.getBoard());
        UUID roomId = UUID.randomUUID();
        roomIdToGame.put(roomId, game);

        Room room = new Room(gameDto.numberOfPlayers(), playersJson, board, 10, 1, 1, -1);
        room.setId(roomId);
        roomRepository.save(room);

        return roomId.toString();
    }

    public synchronized String joinRoom(UUID roomId, String character, String token) throws Exception {

        List<String> characters = getAllCharacters();
        if (!characters.contains(character)) {
            throw new Exception("character \"" + character + "\" is not available");
        }

        List<String> availableCharacters = getAvailableCharacters(roomId);
        if (!availableCharacters.contains(character)) {
            throw new Exception("character is already picked in room \"" + roomId + "\"");
        }

        String clientJson = authService.getClientByToken(token);
        Type clientType = new TypeToken<Client>(){}.getType();
        Client client =  new Gson().fromJson(clientJson, clientType);

        Room room = getRoom(roomId);
        String playersJson = room.getPlayers();
        Type playerListType = new TypeToken<List<Player>>(){}.getType();
        List<Player> players = new Gson().fromJson(playersJson, playerListType);

        for (Player player: players) {
            if (player.getClientId() == client.getId()) {
                throw new Exception("player has already joined room \"" + roomId + "\"");
            }
        }
        Player newPlayer = new Player(client.getId(), PlayerCharacter.fromString(character));
        players.add(newPlayer);
        room.setNumberOfPlayers(room.getNumberOfPlayers() + 1);
        room.setPlayers(new Gson().toJson(players));
        roomRepository.save(room);
        // ADD STATUS UPDATE WHEN ALL JOINED!!!
        return "OK";
    }
}
