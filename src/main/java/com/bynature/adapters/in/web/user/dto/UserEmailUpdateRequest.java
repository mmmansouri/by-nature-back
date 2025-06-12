package com.bynature.adapters.in.web.user.dto;

import com.bynature.domain.model.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEmailUpdateRequest(
        @NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Invalid email format")
        String email
) {
    public Email toEmail() {
        return new Email(email);
    }
}