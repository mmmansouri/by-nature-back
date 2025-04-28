package com.bynature.domain.model;

import com.bynature.domain.exception.CustomerValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomerTest {

    private final Email validEmail = new Email("test@example.com");
    private final PhoneNumber validPhone = new PhoneNumber("+33612345678");
    private final String validFirstName = "John";
    private final String validLastName = "Doe";
    private final UUID validId = UUID.randomUUID();

    @Nested
    @DisplayName("Customer Creation Tests")
    class CustomerCreationTests {
        @Test
        @DisplayName("Should create customer with valid parameters")
        void shouldCreateCustomerWithValidParameters() {
            var customer = new Customer(validFirstName, validLastName, validEmail, validPhone);
            customer.validate();

            assertThat(customer.getId()).isNotNull();
            assertThat(customer.getFirstName()).isEqualTo(validFirstName);
            assertThat(customer.getLastName()).isEqualTo(validLastName);
            assertThat(customer.getEmail()).isEqualTo(validEmail);
            assertThat(customer.getPhoneNumber()).isEqualTo(validPhone);
        }

        @Test
        @DisplayName("Should create customer with provided ID")
        void shouldCreateCustomerWithProvidedId() {
            var customer = new Customer(validId, validFirstName, validLastName, validEmail, validPhone);
            customer.validate();

            assertThat(customer.getId()).isEqualTo(validId);
        }
    }

    @Nested
    @DisplayName("Customer Validation Tests")
    class CustomerValidationTests {
        @Test
        @DisplayName("Should throw exception when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> {
                        Customer invalidCustomer = new Customer(null, validFirstName, validLastName, validEmail, validPhone);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("L'ID du client ne peut pas être null"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when firstName is invalid")
        void shouldThrowExceptionWhenFirstNameIsInvalid(String invalidFirstName) {
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> {
                        Customer invalidCustomer = new Customer(invalidFirstName, validLastName, validEmail, validPhone);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le prénom ne peut pas être vide"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when lastName is invalid")
        void shouldThrowExceptionWhenLastNameIsInvalid(String invalidLastName) {
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> {
                        Customer invalidCustomer = new Customer(validFirstName, invalidLastName, validEmail, validPhone);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le nom ne peut pas être vide"));
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> {
                        Customer invalidCustomer = new Customer(validFirstName, validLastName, null, validPhone);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("L'email ne peut pas être null"));
        }

        @Test
        @DisplayName("Should throw exception when phone is null")
        void shouldThrowExceptionWhenPhoneIsNull() {
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> {
                        Customer invalidCustomer = new Customer(validFirstName, validLastName, validEmail, null);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le numéro de téléphone ne peut pas être null"));
        }
    }

    @Nested
    @DisplayName("Address Field Validation Tests")
    class AddressFieldValidationTests {
        private Customer createValidCustomer() {
            return new Customer(validFirstName, validLastName, validEmail, validPhone);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when streetNumber is empty")
        void shouldThrowExceptionWhenStreetNumberIsEmpty(String invalidStreetNumber) {
            var customer = createValidCustomer();
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setStreetNumber(invalidStreetNumber))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le numéro de rue ne peut pas être vide"));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when street is empty")
        void shouldThrowExceptionWhenStreetIsEmpty(String invalidStreet) {
            var customer = createValidCustomer();
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setStreet(invalidStreet))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le nom de rue ne peut pas être vide"));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when city is empty")
        void shouldThrowExceptionWhenCityIsEmpty(String invalidCity) {
            var customer = createValidCustomer();
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setCity(invalidCity))
                    .satisfies(e -> assertThat(e.getViolations()).contains("La ville ne peut pas être vide"));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when region is empty")
        void shouldThrowExceptionWhenRegionIsEmpty(String invalidRegion) {
            var customer = createValidCustomer();
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setRegion(invalidRegion))
                    .satisfies(e -> assertThat(e.getViolations()).contains("La région ne peut pas être vide"));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when postalCode is empty")
        void shouldThrowExceptionWhenPostalCodeIsEmpty(String invalidPostalCode) {
            var customer = createValidCustomer();
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setPostalCode(invalidPostalCode))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le code postal ne peut pas être vide"));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when country is empty")
        void shouldThrowExceptionWhenCountryIsEmpty(String invalidCountry) {
            var customer = createValidCustomer();
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setCountry(invalidCountry))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le pays ne peut pas être vide"));
        }

        @Test
        @DisplayName("Should set valid address fields")
        void shouldSetValidAddressFields() {
            var customer = createValidCustomer();

            assertDoesNotThrow(() -> {
                customer.setStreetNumber("123");
                customer.setStreet("Main Street");
                customer.setCity("Paris");
                customer.setRegion("Île-de-France");
                customer.setPostalCode("75001");
                customer.setCountry("France");
            });

            assertThat(customer.getStreetNumber()).isEqualTo("123");
            assertThat(customer.getStreet()).isEqualTo("Main Street");
            assertThat(customer.getCity()).isEqualTo("Paris");
            assertThat(customer.getRegion()).isEqualTo("Île-de-France");
            assertThat(customer.getPostalCode()).isEqualTo("75001");
            assertThat(customer.getCountry()).isEqualTo("France");
        }
    }
}