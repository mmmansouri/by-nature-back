package com.bynature.adapters.in.web.user.dto;

import com.bynature.domain.model.User;

import java.util.UUID;

public record UserRetrievalResponse(
        UUID id,
        String email,
        boolean active,
        String role,
        UUID customerId,
        String lastLoginAt,
        String createdAt,
        String updatedAt
) {
    public static UserRetrievalResponse fromDomain(User user) {
        return new UserRetrievalResponse(
                user.getId(),
                user.getEmail().email(),
                user.isActive(),
                user.getRole().name(),
                user.getCustomer() != null ? user.getCustomer().getId() : null,
                user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null,
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
        );
    }
}