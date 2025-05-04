package com.bynature.domain.model;

import com.bynature.domain.exception.ShippingAddressValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private static final PhoneNumber validPhone = new PhoneNumber("+33612345678");
    private static final Email validEmail = new Email("test@example.com");
    private static final Customer validCustomer = new Customer("John", "Doe", validEmail, validPhone);

    @Test
    @DisplayName("Should create shipping address with valid data")
    void shouldCreateShippingAddressWithValidData() {
        var shippingAddress = assertDoesNotThrow(() -> new ShippingAddress(validCustomer, "My Address",
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
                id, validCustomer,"My Address","John", "Doe", validPhone, validEmail,
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
                .isThrownBy(() -> new ShippingAddress(validCustomer,"My Address",
                        firstName, "Doe", validPhone, validEmail,
                        "123", "Main Street", "Paris", "Île-de-France",
                        "75001", "France"));
    }

    @Test
    @DisplayName("Should update updatedAt field")
    void shouldUpdateUpdatedAtField() {
        var shippingAddress = new ShippingAddress(validCustomer,"My Address",
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

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should throw exception when label is invalid")
    void shouldThrowExceptionWhenLabelIsInvalid(String label) {
        assertThatExceptionOfType(ShippingAddressValidationException.class)
                .isThrownBy(() -> new ShippingAddress(validCustomer, label,
                        "John", "Doe", validPhone, validEmail,
                        "123", "Main Street", "Paris", "Île-de-France",
                        "75001", "France"));
    }

    @Test
    @DisplayName("Should update label field")
    void shouldUpdateLabelField() {
        var shippingAddress = new ShippingAddress(validCustomer, "Home",
                "John", "Doe", validPhone, validEmail,
                "123", "Main Street", "Paris", "Île-de-France",
                "75001", "France");

        var newLabel = "Work Address";
        shippingAddress.setLabel(newLabel);

        assertThat(shippingAddress.getLabel()).isEqualTo(newLabel);
    }

    @Nested
    @DisplayName("Date Validation Tests")
    class DateValidationTests {

        @Test
        @DisplayName("Should have updatedAt equal to createdAt upon initial creation")
        void shouldHaveUpdatedAtEqualToCreatedAtUponInitialCreation() {
            var shippingAddress = new ShippingAddress(validCustomer, "My Address",
                    "John", "Doe", validPhone, validEmail,
                    "123", "Main Street", "Paris", "Île-de-France",
                    "75001", "France");

            assertThat(shippingAddress.getUpdatedAt()).isEqualTo(shippingAddress.getCreatedAt());
        }

        @Test
        @DisplayName("Should allow updatedAt to be equal to createdAt")
        void shouldAllowUpdatedAtToBeEqualToCreatedAt() {
            var shippingAddress = new ShippingAddress(validCustomer, "My Address",
                    "John", "Doe", validPhone, validEmail,
                    "123", "Main Street", "Paris", "Île-de-France",
                    "75001", "France");

            assertDoesNotThrow(() -> {
                shippingAddress.setUpdatedAt(shippingAddress.getCreatedAt());
            });
        }

        @Test
        @DisplayName("Should allow updatedAt to be after createdAt")
        void shouldAllowUpdatedAtToBeAfterCreatedAt() {
            var shippingAddress = new ShippingAddress(validCustomer, "My Address",
                    "John", "Doe", validPhone, validEmail,
                    "123", "Main Street", "Paris", "Île-de-France",
                    "75001", "France");

            assertDoesNotThrow(() -> {
                shippingAddress.setUpdatedAt(shippingAddress.getCreatedAt().plusSeconds(1));
            });
        }

        @Test
        @DisplayName("Should reject updatedAt before createdAt")
        void shouldRejectUpdatedAtBeforeCreatedAt() {
            var shippingAddress = new ShippingAddress(validCustomer, "My Address",
                    "John", "Doe", validPhone, validEmail,
                    "123", "Main Street", "Paris", "Île-de-France",
                    "75001", "France");

            assertThatExceptionOfType(ShippingAddressValidationException.class)
                    .isThrownBy(() -> {
                        shippingAddress.setUpdatedAt(shippingAddress.getCreatedAt().minusSeconds(1));
                    })
                    .satisfies(e -> assertThat(e.getViolations())
                            .contains("La date de mise à jour ne peut pas être avant celle de la création"));
        }
    }
}