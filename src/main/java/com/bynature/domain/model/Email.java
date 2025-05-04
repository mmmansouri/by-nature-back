package com.bynature.domain.model;

import com.bynature.domain.exception.EmailValidationException;

import java.util.List;

public record Email(String email) {
    public Email {
        if (email == null || email.isBlank()) {
            throw new EmailValidationException(List.of("Email address cannot be null or blank"));
        }
        String emailRegex = "^[A-Za-z0-9]+([\\.+_-][A-Za-z0-9]+)*@" +
                "[A-Za-z0-9]+([A-Za-z0-9-]*[A-Za-z0-9])?" +
                "(\\.[A-Za-z0-9]+([A-Za-z0-9-]*[A-Za-z0-9])?)+$";
        if (!email.matches(emailRegex)) {
            throw new EmailValidationException(List.of("Invalid email address: " + email));
        }
    }
}