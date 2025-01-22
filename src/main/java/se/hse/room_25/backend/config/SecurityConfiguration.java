package se.hse.room_25.backend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import se.hse.room_25.backend.service.JwtService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    JwtService jwtService;

    @Autowired
    public void prepare(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers( "/auth/**", "/error", "/portfolio/**")
                .permitAll()
                .anyRequest()
                .authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("Unauthorized access attempt: " + request.getRequestURI());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    System.out.println("Access denied for user: " + request.getUserPrincipal().getName());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
