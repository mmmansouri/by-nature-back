package com.bynature.adapters.in.web.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        String message
) {}