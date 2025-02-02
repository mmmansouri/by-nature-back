package com.bynature.domain.model;

public record Email(String email) {
    public Email {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be null or blank");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
    }
}