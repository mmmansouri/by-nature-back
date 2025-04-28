package com.bynature.domain.model;

import com.bynature.domain.exception.ShippingAddressValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ShippingAddressTest {

    private final PhoneNumber validPhone = new PhoneNumber("+33612345678");
    private final Email validEmail = new Email("test@example.com");

    @Test
    @DisplayName("Should create shipping address with valid data")
    void shouldCreateShippingAddressWithValidData() {
        var shippingAddress = assertDoesNotThrow(() -> new ShippingAddress(
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France"));

        assertThat(shippingAddress.getId()).isNotNull();
        assertThat(shippingAddress.getFirstName()).isEqualTo("John");
        assertThat(shippingAddress.getLastName()).isEqualTo("Doe");
        assertThat(shippingAddress.getPhoneNumber()).isEqualTo(validPhone);
        assertThat(shippingAddress.getEmail()).isEqualTo(validEmail);
        assertThat(shippingAddress.getStreetNumber()).isEqualTo("123");
        assertThat(shippingAddress.getStreet()).isEqualTo("Main Street");
        assertThat(shippingAddress.getCity()).isEqualTo("Paris");
        assertThat(shippingAddress.getRegion()).isEqualTo("Île-de-France");
        assertThat(shippingAddress.getPostalCode()).isEqualTo("75001");
        assertThat(shippingAddress.getCountry()).isEqualTo("France");
        assertThat(shippingAddress.getCreatedAt()).isNotNull();
        assertThat(shippingAddress.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should create shipping address with provided ID and timestamps")
    void shouldCreateShippingAddressWithProvidedIdAndTimestamps() {
        var id = UUID.randomUUID();
        var createdAt = LocalDateTime.now().minusDays(1);
        var updatedAt = LocalDateTime.now();

        var shippingAddress = assertDoesNotThrow(() -> new ShippingAddress(
                id, "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France", createdAt, updatedAt));

        assertThat(shippingAddress.getId()).isEqualTo(id);
        assertThat(shippingAddress.getCreatedAt()).isEqualTo(createdAt);
        assertThat(shippingAddress.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should throw exception when firstName is invalid")
    void shouldThrowExceptionWhenFirstNameIsInvalid(String firstName) {
        assertThatExceptionOfType(ShippingAddressValidationException.class)
                .isThrownBy(() -> new ShippingAddress(
                        firstName, "Doe", validPhone, validEmail,
                        "123", "Main Street", "Paris", "Île-de-France",
                        "75001", "France"));
    }

    @Test
    @DisplayName("Should update updatedAt field")
    void shouldUpdateUpdatedAtField() {
        var shippingAddress = new ShippingAddress(
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        var previousUpdatedAt = shippingAddress.getUpdatedAt();
        var newUpdatedAt = LocalDateTime.now().plusHours(1);

        shippingAddress.setUpdatedAt(newUpdatedAt);

        assertThat(shippingAddress.getUpdatedAt())
                .isEqualTo(newUpdatedAt)
                .isNotEqualTo(previousUpdatedAt);
    }

    // Additional tests for all other fields would follow the same pattern
}