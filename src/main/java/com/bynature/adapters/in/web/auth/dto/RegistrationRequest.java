package com.bynature.adapters.in.web.auth.dto;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.Role;
import com.bynature.domain.model.User;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
        @NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
    public User toDomain() {
        return new User(new Email(email), password, Role.CUSTOMER);
    }
}