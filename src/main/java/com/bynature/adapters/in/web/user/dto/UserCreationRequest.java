package com.bynature.adapters.in.web.user.dto;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.Role;
import com.bynature.domain.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserCreationRequest(
        @NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "Password must be at least 8 characters and contain at least one digit, " +
                        "one lowercase letter, one uppercase letter, one special character, and no whitespace"
        )
        String password,

        @NotNull(message = "Role is required")
        Role role
) {
    public User toDomain() {
        return new User(new Email(email), password, role);
    }
}
