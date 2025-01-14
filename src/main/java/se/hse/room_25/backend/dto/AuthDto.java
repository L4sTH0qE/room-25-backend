package se.hse.room_25.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthDTO(@NotBlank(message = "username must be not blank string")
                      String username,
                      @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!?@#$%^&*\\-_+(){}\\[\\]<>/\\\\|\"'.:,]).{8,64}",
                              message = "password must be between 8-64 characters and contain uppercase letter, lowercase letter, number and special character")
                      String password) {
}
