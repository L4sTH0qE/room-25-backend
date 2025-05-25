package se.hse.room_25.backend.room;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import se.hse.room_25.backend.dto.AuthDto;
import se.hse.room_25.backend.service.AuthService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;

    private String loginAndGetToken(String username, String password) throws Exception {
        authService.register(new AuthDto(username, password));
        String body = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
        ).andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    @Test
    public void testCreateRoomAndJoin() throws Exception {
        String token = loginAndGetToken("player1", "Pa$$word123");

        String json = """
                    {"numberOfPlayers":2,
                     "character":"FRANK",
                     "gameMode":"COOP",
                     "difficulty":"EASY"}
                """;
        MvcResult res = mockMvc.perform(post("/room")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String roomId = JsonPath.read(res.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/room/" + roomId + "/characters"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("JENNIFER"))); // etc

        mockMvc.perform(get("/room/check/" + roomId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.already_joined").value(true));
    }
}
