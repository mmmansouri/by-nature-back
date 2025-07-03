package com.bynature.adapters.in.web.auth.dto;

import java.util.UUID;

public record UserRegistrationResponse(UUID userId, String message) {
}