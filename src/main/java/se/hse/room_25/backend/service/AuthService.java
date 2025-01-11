package se.hse.room_25.backend.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.hse.room_25.backend.dto.AuthDTO;
import se.hse.room_25.backend.entity.Client;
import se.hse.room_25.backend.entity.Session;
import se.hse.room_25.backend.repository.ClientRepository;
import se.hse.room_25.backend.repository.SessionRepository;

import java.sql.Timestamp;
import java.util.Optional;

@Log4j2
@Service
public class AuthService {

    private ClientRepository clientRepository;
    private SessionRepository sessionRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    /// Initialise necessary Spring beans.
    @Autowired
    public void prepare(ClientRepository clientRepository, SessionRepository sessionRepository,
                        PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                        JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    /// Register new client.
    ///
    /// @param authDTO DTO object containing registration data.
    /// @throws Exception if a client with the same username already exists.
    /// @return Message about registration success.
    public synchronized String register(AuthDTO authDTO) throws Exception {

        if (clientRepository.findByUsername(authDTO.username()).isPresent()) {
            throw new Exception("username is already in use");
        }

        Client client = new Client(authDTO.username(), passwordEncoder.encode(authDTO.password()));
        clientRepository.save(client);

        return "user registered successfully";
    }

    /// Login existing client.
    ///
    /// @param authDTO DTO object containing login data.
    /// @throws Exception if no client was found with given username or password is incorrect.
    /// @return token to authorize with.
    public String login(AuthDTO authDTO) throws Exception {

        Optional<Client> client = clientRepository.findByUsername(authDTO.username());

        if (client.isEmpty()) {
            throw new Exception("no user found with such username");
        }
        if (!passwordEncoder.matches(authDTO.password(), client.get().getPassword())) {
            throw new Exception("password is incorrect");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.username(), authDTO.password())
        );
        String token = jwtService.generateToken(authentication);

        Session session = new Session(client.get(), token, new Timestamp(jwtService.extractAllClaims(token).getExpiration().getTime()));
        sessionRepository.save(session);

        return token;
    }

    /// Retrieve a client info by session token.
    ///
    /// @param token The token of the client's session.
    /// @throws Exception if no client was found with given token.
    /// @return The String object representing the client.
    public String getClientByToken(String token) throws Exception {

        // Get the session by the JWT token
        Optional<Session> session = sessionRepository.findByToken(token);

        if (session.isEmpty()) {
            throw new Exception("invalid token");
        }

        // Validate and parse the JWT token
        if (!jwtService.isValid(token)) {
            throw new Exception("expired token");
        }

        // Get the client
        Client client = session.get().getClient();

        return "{\"id\":\"" + client.getId() + "\",\n\"username\":\"" + client.getUsername() + "\",\n\"password\":\"" + client.getPassword() + "\"}";
    }
}
