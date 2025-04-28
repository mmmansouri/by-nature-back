package com.bynature.domain.model;

import com.bynature.domain.exception.EmailValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailTest {

    @Test
    @DisplayName("Should create email with valid address")
    void shouldCreateEmailWithValidAddress() {
        var validEmail = "test@example.com";
        var email = assertDoesNotThrow(() -> new Email(validEmail));
        assertThat(email.email()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test@domain.com",
            "user.name@domain.com",
            "user-name@domain.com",
            "username@domain.co.uk",
            "user_name@domain.com",
            "username@domain-domain.com",
            "user+name@domain.com",  // Gmail allows + in emails
            "user.name+tag@domain.com"
    })
    @DisplayName("Should create email with various valid formats")
    void shouldCreateEmailWithVariousValidFormats(String validEmail) {
        var email = assertDoesNotThrow(() -> new Email(validEmail));
        assertThat(email.email()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Should throw exception when email is null or blank")
    void shouldThrowExceptionWhenEmailIsNullOrBlank(String invalidEmail) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Email(invalidEmail))
                .withMessageContaining("Email address cannot be null or blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plainaddress",
            "@domain.com",
            "user@",
            "user@.com",
            "user@domain..com",
            "user name@domain.com",
            "user@domain@domain.com",
            ".user@domain.com",
            "user.@domain.com",
            "user..name@domain.com",
            "user@domain.com.",
            "user@domain_domain.com",
            "user@-domain.com"
    })
    @DisplayName("Should throw exception when email format is invalid")
    void shouldThrowExceptionWhenEmailFormatIsInvalid(String invalidEmail) {
        assertThatExceptionOfType(EmailValidationException.class)
                .isThrownBy(() -> new Email(invalidEmail))
                .withMessageContaining("Invalid email address");
    }
}