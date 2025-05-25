package se.hse.room_25.backend.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.hse.room_25.backend.dto.AuthDto;
import se.hse.room_25.backend.service.AuthService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void registerAndLoginTest() throws Exception {
        String username = "newuser";
        String pwd = "StrongPa$$99";
        AuthDto dto = new AuthDto(username, pwd);
        authService.register(dto);

        String token = authService.login(dto);
        assertNotNull(token);
        String info = authService.getClientByToken(token);
        assertTrue(info.contains(username));
    }
}
