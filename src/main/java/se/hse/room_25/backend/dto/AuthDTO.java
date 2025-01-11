package se.hse.room_25.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthDTO(@NotBlank(message = "Username must be not blank string")
                      String username,
                      @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!?@#$%^&*\\-_+(){}\\[\\]<>/\\\\|\"'.:,]).{8,64}",
                              message = "Password must be between 8-64 characters and contain uppercase letter, lowercase letter, number and special character")
                      String password) {
}
