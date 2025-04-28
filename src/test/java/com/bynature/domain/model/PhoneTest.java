package com.bynature.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PhoneTest {

    @Test
    @DisplayName("Should create phone number with valid French number")
    void shouldCreatePhoneNumberWithValidNumber() {
        var validNumber = "+33612345678";
        var phoneNumber = assertDoesNotThrow(() -> new PhoneNumber(validNumber));
        assertThat(phoneNumber.number()).isEqualTo(validNumber);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+33612345678",
            "+33 6 12 34 56 78",
            "06 12 34 56 78",
            "0612345678"
    })
    @DisplayName("Should create phone number with various valid French formats")
    void shouldCreatePhoneNumberWithVariousValidFormats(String validNumber) {
        assertDoesNotThrow(() -> new PhoneNumber(validNumber));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Should throw exception when phone number is null or blank")
    void shouldThrowExceptionWhenPhoneNumberIsNullOrBlank(String invalidNumber) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PhoneNumber(invalidNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abcdefghij",
            "+33123",
            "123456789012345",
            "+99912345678"  // Invalid country code
    })
    @DisplayName("Should throw exception when phone number format is invalid")
    void shouldThrowExceptionWhenPhoneNumberFormatIsInvalid(String invalidNumber) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PhoneNumber(invalidNumber));
    }
}