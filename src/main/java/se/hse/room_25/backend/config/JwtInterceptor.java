package se.hse.room_25.backend.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import se.hse.room_25.backend.service.JwtService;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.warn("Websocket interceptor started");
        // Только обрабатываем STOMP CONNECT
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if ("CONNECT".equals(Objects.requireNonNull(accessor.getCommand()).name())) {
            // Получаем Authorization header
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (!jwtService.isValid(token)) {
                    throw new IllegalArgumentException("Invalid JWT Token");
                }
                Claims claims = jwtService.extractAllClaims(token);
                accessor.setUser(claims::getSubject);
            } else {
                throw new IllegalArgumentException("Missing or invalid Authorization header in WebSocket CONNECT");
            }
        }
        log.warn("Websocket interceptor finished");
        return message;
    }
}
