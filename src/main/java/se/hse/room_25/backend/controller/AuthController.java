package se.hse.room_25.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import se.hse.room_25.backend.dto.AuthDto;
import se.hse.room_25.backend.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    /// Initialise necessary Spring beans.
    @Autowired
    public void prepare(AuthService authService) {
        this.authService = authService;
    }

    /// Endpoint for user login.
    ///
    /// @param authDTO DTO object containing login data.
    /// @return ResponseEntity containing the login response.
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthDto authDTO, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"error\":\"неверный логин или пароль\"}");
        }

        try {
            String token = authService.login(authDTO);
            return ResponseEntity.ok("{\"token\":\"" + token + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    /// Endpoint for user registration.
    ///
    /// @param authDTO DTO object containing registration data.
    /// @return ResponseEntity containing the registration response.
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid AuthDto authDTO, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("{\"error\":\"неверный логин или пароль\"}");
        }

        try {
            String message = authService.register(authDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\":\"" + message + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    /// Endpoint for getting user info by token.
    ///
    /// @param authHeader 'Authorization' header value (e.g. 'Bearer 1234').
    /// @return ResponseEntity containing user info.
    @GetMapping("/get")
    public ResponseEntity<String> getClientByToken(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"" + "неверный заголовок" + "\"}");
        }
        String token = authHeader.substring(7);

        try {
            String result = authService.getClientByToken(token);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}
