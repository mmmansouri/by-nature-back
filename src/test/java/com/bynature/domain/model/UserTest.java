package com.bynature.domain.model;

import com.bynature.domain.exception.UserValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UserTest {

    private final Email validEmail = new Email("user@example.com");
    private final String validPassword = "securePassword123";
    private final Role validRole = Role.CUSTOMER;
    private final User validUser = new User(validEmail, validPassword, validRole);
    private final Customer validCustomer = new Customer(validUser,
            "John", "Doe", new Email("john@example.com"), new PhoneNumber("+33612345678"),
            "123", "Main St", "Paris", "Île-de-France", "75001", "France"
    );
    private final UUID validId = UUID.randomUUID();
    private final LocalDateTime validCreatedAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime validUpdatedAt = LocalDateTime.now();
    private final LocalDateTime validLastLoginAt = LocalDateTime.now();

    @Nested
    @DisplayName("User Creation Tests")
    class UserCreationTests {
        @Test
        @DisplayName("Should create user with email, password and role")
        void shouldCreateUserWithEmailPasswordAndRole() {
            var user = new User(validEmail, validPassword, validRole);

            assertThat(user.getId()).isNotNull();
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getPassword()).isEqualTo(validPassword);
            assertThat(user.getRole()).isEqualTo(validRole);
            assertThat(user.isActive()).isTrue();
            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
        }

        @Test
        @DisplayName("Should create user with email, password and customer")
        void shouldCreateUserWithEmailPasswordAndCustomer() {
            var user = new User(validEmail, validPassword, validCustomer);

            assertThat(user.getId()).isNotNull();
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getPassword()).isEqualTo(validPassword);
            assertThat(user.getCustomer()).isEqualTo(validCustomer);
            assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(user.isActive()).isTrue();
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
        }

        @Test
        @DisplayName("Should create user with all parameters")
        void shouldCreateUserWithAllParameters() {
            var user = new User(
                    validId, validEmail, validPassword, validCustomer, true, validRole,
                    validLastLoginAt, validCreatedAt, validUpdatedAt
            );

            assertThat(user.getId()).isEqualTo(validId);
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getPassword()).isEqualTo(validPassword);
            assertThat(user.getCustomer()).isEqualTo(validCustomer);
            assertThat(user.isActive()).isTrue();
            assertThat(user.getRole()).isEqualTo(validRole);
            assertThat(user.getLastLoginAt()).isEqualTo(validLastLoginAt);
            assertThat(user.getCreatedAt()).isEqualTo(validCreatedAt);
            assertThat(user.getUpdatedAt()).isEqualTo(validUpdatedAt);
        }
    }

    @Nested
    @DisplayName("User Validation Tests")
    class UserValidationTests {
        @Test
        @DisplayName("Should throw exception when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(null, validEmail, validPassword, validCustomer, true, validRole,
                                    validLastLoginAt, validCreatedAt, validUpdatedAt)
                    )
                    .satisfies(e -> assertThat(e.getViolations()).contains("User ID cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(validId, null, validPassword, validCustomer, true, validRole,
                                    validLastLoginAt, validCreatedAt, validUpdatedAt)
                    )
                    .satisfies(e -> assertThat(e.getViolations()).contains("Email cannot be null"));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Should throw exception when password is null")
        void shouldThrowExceptionWhenPasswordIsNull(String invalidPassword) {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(validId, validEmail, invalidPassword, validCustomer, true, validRole,
                                    validLastLoginAt, validCreatedAt, validUpdatedAt)
                    )
                    .satisfies(e -> assertThat(e.getViolations()).contains("Password cannot be empty"));
        }

        @Test
        @DisplayName("Should throw exception when password is empty")
        void shouldThrowExceptionWhenPasswordIsEmpty() {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(validId, validEmail, "", validCustomer, true, validRole,
                                    validLastLoginAt, validCreatedAt, validUpdatedAt)
                    )
                    .satisfies(e -> assertThat(e.getViolations()).contains("Password cannot be empty"));
        }

        @Test
        @DisplayName("Should throw exception when role is null")
        void shouldThrowExceptionWhenRoleIsNull() {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(validId, validEmail, validPassword, validCustomer, true, null,
                                    validLastLoginAt, validCreatedAt, validUpdatedAt)
                    )
                    .satisfies(e -> assertThat(e.getViolations()).contains("Role cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when createdAt is null")
        void shouldThrowExceptionWhenCreatedAtIsNull() {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(validId, validEmail, validPassword, validCustomer, true, validRole,
                                    validLastLoginAt, null, validUpdatedAt)
                    )
                    .satisfies(e -> assertThat(e.getViolations()).contains("Creation date cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when updatedAt is null")
        void shouldThrowExceptionWhenUpdatedAtIsNull() {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(validId, validEmail, validPassword, validCustomer, true, validRole,
                                    validLastLoginAt, validCreatedAt, null)
                    )
                    .satisfies(e -> assertThat(e.getViolations()).contains("Updated date cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when updatedAt is before createdAt")
        void shouldThrowExceptionWhenUpdatedAtIsBeforeCreatedAt() {
            assertThatExceptionOfType(UserValidationException.class)
                    .isThrownBy(() ->
                            new User(validId, validEmail, validPassword, validCustomer, true, validRole,
                                    validLastLoginAt, validCreatedAt, validCreatedAt.minusDays(1))
                    )
                    .satisfies(e -> assertThat(e.getViolations())
                            .contains("La date de mise à jour ne peut pas être avant celle de la création"));
        }
    }

    @Nested
    @DisplayName("User Update Tests")
    class UserUpdateTests {
        private User createValidUser() {
            return new User(validEmail, validPassword, validRole);
        }

        @Test
        @DisplayName("Should update password successfully")
        void shouldUpdatePasswordSuccessfully() {
            var user = createValidUser();
            String newPassword = "newPassword123";

            user.setPassword(newPassword);

            assertThat(user.getPassword()).isEqualTo(newPassword);
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(user.getCreatedAt());
        }

        @ParameterizedTest
        @EnumSource(Role.class)
        @DisplayName("Should update role successfully")
        void shouldUpdateRoleSuccessfully(Role newRole) {
            var user = createValidUser();

            user.setRole(newRole);

            assertThat(user.getRole()).isEqualTo(newRole);
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(user.getCreatedAt());
        }

        @Test
        @DisplayName("Should update lastLoginAt successfully")
        void shouldUpdateLastLoginAtSuccessfully() {
            var user = createValidUser();
            LocalDateTime newLastLogin = LocalDateTime.now().plusHours(1);

            user.setLastLoginAt(newLastLogin);

            assertThat(user.getLastLoginAt()).isEqualTo(newLastLogin);
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(user.getCreatedAt());
        }

        @Test
        @DisplayName("Should update active status successfully")
        void shouldUpdateActiveStatusSuccessfully() {
            var user = createValidUser();

            user.setActive(false);

            assertThat(user.isActive()).isFalse();
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(user.getCreatedAt());
        }

        @Test
        @DisplayName("Should update email successfully")
        void shouldUpdateEmailSuccessfully() {
            var user = createValidUser();
            Email newEmail = new Email("updated@example.com");

            user.setEmail(newEmail);

            assertThat(user.getEmail()).isEqualTo(newEmail);
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(user.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("User Customer Link Tests")
    class UserCustomerLinkTests {
        @Test
        @DisplayName("Should link user to customer successfully")
        void shouldLinkUserToCustomerSuccessfully() {
            var user = new User(validEmail, validPassword, validRole);
            assertThat(user.getCustomer()).isNull();

            user.linkToCustomer(validCustomer);

            assertThat(user.getCustomer()).isEqualTo(validCustomer);
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(user.getCreatedAt());
        }

        @Test
        @DisplayName("Should create user already linked to customer")
        void shouldCreateUserAlreadyLinkedToCustomer() {
            var user = new User(validEmail, validPassword, validCustomer);

            assertThat(user.getCustomer()).isEqualTo(validCustomer);
            assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
        }
    }
}