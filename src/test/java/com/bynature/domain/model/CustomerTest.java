package com.bynature.domain.model;

import com.bynature.domain.exception.CustomerValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomerTest {

    private final Email validEmail = new Email("test@example.com");
    private final PhoneNumber validPhone = new PhoneNumber("+33612345678");
    private final String validFirstName = "John";
    private final String validLastName = "Doe";
    private final String validStreetNumber = "123";
    private final String validStreet = "Main St";
    private final String validCity = "Paris";
    private final String validRegion = "Île-de-France";
    private final String validPostalCode = "75001";
    private final String validCountry = "France";
    private final UUID validId = UUID.randomUUID();
    private final User validUser = new User(validEmail, "password",Role.CUSTOMER);

    @Nested
    @DisplayName("Customer Creation Tests")
    class CustomerCreationTests {
        @Test
        @DisplayName("Should create customer with valid parameters")
        void shouldCreateCustomerWithValidParameters() {
            var customer = new Customer(validUser, validFirstName, validLastName, validEmail, validPhone,
                    validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);
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
            var customer = new Customer(validId,
                    validUser,
                    validFirstName,
                    validLastName,
                    validEmail,
                    validPhone,
                    LocalDateTime.now(),
                    LocalDateTime.now()
                    );
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
                        Customer invalidCustomer = new Customer(null,
                                validUser,
                                validFirstName,
                                validLastName,
                                validEmail,
                                validPhone,
                                LocalDateTime.now(),
                                LocalDateTime.now());
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
                        Customer invalidCustomer = new Customer(validUser,invalidFirstName, validLastName, validEmail, validPhone,
                                validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);
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
                        Customer invalidCustomer = new Customer(validUser,validFirstName, invalidLastName, validEmail, validPhone
                        , validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le nom ne peut pas être vide"));
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> {
                        Customer invalidCustomer = new Customer(validUser,validFirstName, validLastName, null, validPhone,
                                validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("L'email ne peut pas être null"));
        }

        @Test
        @DisplayName("Should throw exception when phone is null")
        void shouldThrowExceptionWhenPhoneIsNull() {
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> {
                        Customer invalidCustomer = new Customer(validUser,validFirstName, validLastName, validEmail, null,
                                validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);
                        invalidCustomer.validate();
                    })
                    .satisfies(e -> assertThat(e.getViolations()).contains("Le numéro de téléphone ne peut pas être null"));
        }
    }

    @Nested
    @DisplayName("Address Field Validation Tests")
    class AddressFieldValidationTests {
        private Customer createValidCustomer() {
            return new Customer(validUser,validFirstName, validLastName, validEmail, validPhone,
                    validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);
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

        @Test
        @DisplayName("Should create customer with complete address")
        void shouldCreateCustomerWithCompleteAddress() {
            var customer = new Customer(validUser,validFirstName, validLastName, validEmail, validPhone,
                    validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);

            // Set address fields
            customer.setStreetNumber("42");
            customer.setStreet("Rue de Paris");
            customer.setCity("Lyon");
            customer.setRegion("Rhône-Alpes");
            customer.setPostalCode("69001");
            customer.setCountry("France");

            // Verify all fields are set correctly
            assertThat(customer.getStreetNumber()).isEqualTo("42");
            assertThat(customer.getStreet()).isEqualTo("Rue de Paris");
            assertThat(customer.getCity()).isEqualTo("Lyon");
            assertThat(customer.getRegion()).isEqualTo("Rhône-Alpes");
            assertThat(customer.getPostalCode()).isEqualTo("69001");
            assertThat(customer.getCountry()).isEqualTo("France");
        }

        @Test
        @DisplayName("Should update address fields correctly")
        void shouldUpdateAddressFieldsCorrectly() {
            var customer = new Customer(validUser,validFirstName, validLastName, validEmail, validPhone,
                    validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);

            // Set initial address
            customer.setStreetNumber("10");
            customer.setStreet("Old Street");
            customer.setCity("Old City");
            customer.setRegion("Old Region");
            customer.setPostalCode("00000");
            customer.setCountry("Old Country");

            // Update all address fields
            LocalDateTime beforeUpdate = LocalDateTime.now();
            customer.setStreetNumber("42");
            customer.setStreet("New Street");
            customer.setCity("New City");
            customer.setRegion("New Region");
            customer.setPostalCode("12345");
            customer.setCountry("New Country");
            customer.setUpdatedAt(LocalDateTime.now());

            // Verify fields are updated
            assertThat(customer.getStreetNumber()).isEqualTo("42");
            assertThat(customer.getStreet()).isEqualTo("New Street");
            assertThat(customer.getCity()).isEqualTo("New City");
            assertThat(customer.getRegion()).isEqualTo("New Region");
            assertThat(customer.getPostalCode()).isEqualTo("12345");
            assertThat(customer.getCountry()).isEqualTo("New Country");
            assertThat(customer.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
        }

        @Test
        @DisplayName("Should validate all address fields when updating")
        void shouldValidateAllAddressFieldsWhenUpdating() {
            var customer = new Customer(validUser,validFirstName, validLastName, validEmail, validPhone,
                    validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);

            // Test each validation separately
            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setStreetNumber("  "));

            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setStreet(""));

            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setCity("\t"));

            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setRegion("\n"));

            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setPostalCode(" "));

            assertThatExceptionOfType(CustomerValidationException.class)
                    .isThrownBy(() -> customer.setCountry(""));
        }
    }

    @Test
    @DisplayName("Should have updatedAt equal to createdAt upon initial creation")
    void shouldHaveUpdatedAtEqualToCreatedAtUponInitialCreation() {
        var customer = new Customer(validUser, validFirstName, validLastName, validEmail, validPhone,
                validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);

        assertThat(customer.getUpdatedAt()).isEqualTo(customer.getCreatedAt());
    }

    @Test
    @DisplayName("Should allow updatedAt to be equal to createdAt")
    void shouldAllowUpdatedAtToBeEqualToCreatedAt() {
        var customer = new Customer(validUser, validFirstName, validLastName, validEmail, validPhone,
                validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);

        assertDoesNotThrow(() -> {
            customer.setUpdatedAt(customer.getCreatedAt());
        });
    }

    @Test
    @DisplayName("Should allow updatedAt to be after createdAt")
    void shouldAllowUpdatedAtToBeAfterCreatedAt() {
        var customer = new Customer(validUser, validFirstName, validLastName, validEmail, validPhone,
                validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);

        assertDoesNotThrow(() -> {
            customer.setUpdatedAt(customer.getCreatedAt().plusSeconds(1));
        });
    }

    @Test
    @DisplayName("Should reject updatedAt before createdAt")
    void shouldRejectUpdatedAtBeforeCreatedAt() {
        var customer = new Customer(validUser, validFirstName, validLastName, validEmail, validPhone,
                validStreetNumber, validStreet, validCity, validRegion, validPostalCode, validCountry);

        assertThatExceptionOfType(CustomerValidationException.class)
                .isThrownBy(() -> {
                    customer.setUpdatedAt(customer.getCreatedAt().minusSeconds(1));
                })
                .satisfies(e -> assertThat(e.getViolations())
                        .contains("La date de mise à jour ne peut pas être avant celle de la création"));
    }
}