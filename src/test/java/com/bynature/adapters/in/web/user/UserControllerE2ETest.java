package com.bynature.adapters.in.web.user;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.user.dto.UserCreationRequest;
import com.bynature.adapters.in.web.user.dto.UserEmailUpdateRequest;
import com.bynature.adapters.in.web.user.dto.UserPasswordUpdateRequest;
import com.bynature.adapters.in.web.user.dto.UserRetrievalResponse;
import com.bynature.domain.model.Role;
import com.bynature.domain.model.User;
import com.bynature.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerE2ETest extends AbstractByNatureTest {

    private static final UUID USER_ID = UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402");

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // Authenticate before each test
        authenticateUser();
    }

    @Test
    @DisplayName("When get existing user, should return user - E2E")
    public void whenGetExistingUser_shouldReturnUser_E2E() {
        User user = userService.getUser(USER_ID);

        ResponseEntity<UserRetrievalResponse> response = restTemplate.exchange(
                "/users/" + USER_ID,
                HttpMethod.GET,
                createAuthenticatedEntity(),
                UserRetrievalResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(USER_ID);
        assertThat(response.getBody().email()).isEqualTo(user.getEmail().email());
        assertThat(response.getBody().active()).isEqualTo(user.isActive());
        assertThat(response.getBody().role()).isEqualTo(user.getRole().name());
        assertThat(response.getBody().createdAt()).isEqualTo(user.getCreatedAt().toString());
        assertThat(response.getBody().updatedAt()).isEqualTo(user.getUpdatedAt().toString());
    }

    @Test
    @DisplayName("When get non-existing user, should return 404 - E2E")
    public void whenGetNonExistingUser_shouldReturn404_E2E() {
        ResponseEntity<UserRetrievalResponse> response = restTemplate.exchange(
                "/users/" + UUID.randomUUID(),
                HttpMethod.GET,
                createAuthenticatedEntity(),
                UserRetrievalResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("When create valid user, should return created - E2E")
    public void whenCreateValidUser_shouldReturnCreated_E2E() {
        UserCreationRequest validRequest = new UserCreationRequest(
                "new.user@example.com",
                "P@ssw0rd1"
        );

        ResponseEntity<UUID> response = restTemplate.exchange(
                "/users",
                HttpMethod.POST,
                createAuthenticatedEntity(validRequest),
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();

        UUID userId = response.getBody();
        ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.exchange(
                "/users/" + userId,
                HttpMethod.GET,
                createAuthenticatedEntity(),
                UserRetrievalResponse.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserRetrievalResponse user = getResponse.getBody();
        assertThat(user).isNotNull();
        assertThat(user.email()).isEqualTo(validRequest.email());
        assertThat(user.role()).isEqualTo(Role.CUSTOMER.toString());
        assertThat(user.active()).isTrue();
    }

    @Test
    @DisplayName("Should successfully activate a user")
    public void whenActivateUser_shouldUpdateActiveStatusToTrue() {
        // First deactivate the user
        restTemplate.exchange(
                "/users/" + USER_ID + "/deactivate",
                HttpMethod.PUT,
                createAuthenticatedEntity(),
                Void.class
        );

        // Then activate the user
        ResponseEntity<Void> response = restTemplate.exchange(
                "/users/" + USER_ID + "/activate",
                HttpMethod.PUT,
                createAuthenticatedEntity(),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify the user was activated
        ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.exchange(
                "/users/" + USER_ID,
                HttpMethod.GET,
                createAuthenticatedEntity(),
                UserRetrievalResponse.class
        );

        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().active()).isTrue();
    }

    @Test
    @DisplayName("Should successfully deactivate a user")
    public void whenDeactivateUser_shouldUpdateActiveStatusToFalse() {
        try {
            // First activate the user
            restTemplate.exchange(
                    "/users/" + USER_ID + "/activate",
                    HttpMethod.PUT,
                    createAuthenticatedEntity(),
                    Void.class
            );

            // Deactivate the user (the operation being tested)
            ResponseEntity<Void> response = restTemplate.exchange(
                    "/users/" + USER_ID + "/deactivate",
                    HttpMethod.PUT,
                    createAuthenticatedEntity(),
                    Void.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            // Verify the user was deactivated
            ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.exchange(
                    "/users/" + USER_ID,
                    HttpMethod.GET,
                    createAuthenticatedEntity(),
                    UserRetrievalResponse.class
            );

            assertThat(getResponse.getBody()).isNotNull();
            assertThat(getResponse.getBody().active()).isFalse();
        } finally {
            // Rollback: Reactivate the user to restore the original state
            ResponseEntity<Void> rollbackResponse = restTemplate.exchange(
                    "/users/" + USER_ID + "/activate",
                    HttpMethod.PUT,
                    createAuthenticatedEntity(),
                    Void.class
            );

            // Verify rollback was successful
            if (rollbackResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
                System.err.println("WARNING: Failed to reactivate user during test rollback");
            }
        }
    }

    private static Stream<Arguments> invalidUserRequests() {
        return Stream.of(
                Arguments.of("Empty email",
                        new UserCreationRequest("", "P@ssw0rd1"),
                        "email", "must not be empty"),
                Arguments.of("Invalid email format",
                        new UserCreationRequest("invalid-email", "P@ssw0rd1"),
                        "email", "must be a well-formed email address"),
                Arguments.of("Empty password",
                        new UserCreationRequest("valid@example.com", ""),
                        "password", "must not be empty"),
                Arguments.of("Weak password - no special character",
                        new UserCreationRequest("valid@example.com", "Password123"),
                        "password", "weak password"),
                Arguments.of("Weak password - no uppercase",
                        new UserCreationRequest("valid@example.com", "password@123"),
                        "password", "weak password"),
                Arguments.of("Weak password - no lowercase",
                        new UserCreationRequest("valid@example.com", "PASSWORD@123"),
                        "password", "weak password"),
                Arguments.of("Weak password - no digit",
                        new UserCreationRequest("valid@example.com", "Password@abc"),
                        "password", "weak password")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUserRequests")
    @DisplayName("Should validate UserCreationRequest fields")
    void shouldValidateUserCreationRequestFields(String testName,
                                                 UserCreationRequest invalidRequest,
                                                 String fieldName,
                                                 String expectedErrorMessage) {
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/users",
                HttpMethod.POST,
                createAuthenticatedEntity(invalidRequest),
                ProblemDetail.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().getProperties();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("validationErrors");

        @SuppressWarnings("unchecked")
        List<String> violations = (List<String>) responseBody.get("validationErrors");

        boolean hasExpectedViolation = violations.stream()
                .anyMatch(violation -> violation.contains(fieldName));

        assertThat(hasExpectedViolation).isTrue();
    }

    @Test
    @DisplayName("Should successfully map valid UserCreationRequest to domain model")
    void shouldMapValidRequestToDomainModel() {
        UserCreationRequest validRequest = new UserCreationRequest(
                "test.user@example.com",
                "P@ssw0rd1"
        );

        User user = validRequest.toDomain();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail().email()).isEqualTo("test.user@example.com");
        assertThat(user.getPassword()).isEqualTo("P@ssw0rd1");
        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(user.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should successfully update user email")
    public void whenUpdateUserEmail_shouldUpdateEmail() {
        // Get original email before updating
        ResponseEntity<UserRetrievalResponse> initialResponse = restTemplate.exchange(
                "/users/" + USER_ID,
                HttpMethod.GET,
                createAuthenticatedEntity(),
                UserRetrievalResponse.class
        );

        assertThat(initialResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(initialResponse.getBody()).isNotNull();
        String originalEmail = initialResponse.getBody().email();

        try {
            // Create email update request
            UserEmailUpdateRequest updateRequest = new UserEmailUpdateRequest("updated.email@example.com");

            // Update the user's email
            ResponseEntity<Void> response = restTemplate.exchange(
                    "/users/" + USER_ID + "/email",
                    HttpMethod.PATCH,
                    createAuthenticatedEntity(updateRequest),
                    Void.class
            );

            // Verify response
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            // Verify the email was updated
            ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.exchange(
                    "/users/" + USER_ID,
                    HttpMethod.GET,
                    createAuthenticatedEntity(),
                    UserRetrievalResponse.class
            );

            assertThat(getResponse.getBody()).isNotNull();
            assertThat(getResponse.getBody().email()).isEqualTo("updated.email@example.com");
        } finally {
            // Rollback - restore the original email
            UserEmailUpdateRequest rollbackRequest = new UserEmailUpdateRequest(originalEmail);

            restTemplate.exchange(
                    "/users/" + USER_ID + "/email",
                    HttpMethod.PATCH,
                    createAuthenticatedEntity(rollbackRequest),
                    Void.class
            );
        }
    }

    @Test
    @DisplayName("Should reject invalid email format")
    public void whenUpdateUserWithInvalidEmail_shouldReturnBadRequest() {
        // Create invalid email update request
        UserEmailUpdateRequest invalidRequest = new UserEmailUpdateRequest("invalid-email");

        // Attempt to update with invalid email
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/users/" + USER_ID + "/email",
                HttpMethod.PATCH,
                createAuthenticatedEntity(invalidRequest),
                ProblemDetail.class
        );

        // Verify bad request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should successfully update user password")
    public void whenUpdateUserPassword_shouldUpdatePassword() {
        // Define the test passwords
        String newTestPassword = "NewStrongP@ssw0rd123";
        String originalPassword = "Str0ngP@ssword123!"; // Original password from test setup

        try {
            // Create password update request
            UserPasswordUpdateRequest updateRequest = new UserPasswordUpdateRequest(
                    originalPassword,  // Current password
                    newTestPassword    // New password
            );

            // Update the password
            ResponseEntity<Void> updateResponse = restTemplate.exchange(
                    "/users/" + USER_ID + "/password",
                    HttpMethod.PUT,
                    createAuthenticatedEntity(updateRequest),
                    Void.class
            );

            // Verify successful update
            assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            // Clear the authentication context - important to force reauthentication!
            this.accessToken = null;

            // Verify we can authenticate with the new password
            String newToken = obtainAccessToken("john.doe@example.com", newTestPassword);
            assertThat(newToken).isNotNull();

        } finally {
            // Restore the original password regardless of test outcome
            // We need to be authenticated again first
            if (this.accessToken == null) {
                authenticateUser("john.doe@example.com", newTestPassword);
            }

            UserPasswordUpdateRequest rollbackRequest = new UserPasswordUpdateRequest(
                    newTestPassword,  // Current password (the one we just set)
                    originalPassword  // Original password to restore
            );

            restTemplate.exchange(
                    "/users/" + USER_ID + "/password",
                    HttpMethod.PUT,
                    createAuthenticatedEntity(rollbackRequest),
                    Void.class
            );

            // Reset token
            this.accessToken = null;
        }
    }

    @Test
    @DisplayName("Should reject weak password")
    public void whenUpdateWithWeakPassword_shouldReturnBadRequest() {
        // Create request with weak password
        UserPasswordUpdateRequest weakPasswordRequest = new UserPasswordUpdateRequest(
                "OldP@ssw0rd",
                "weak"
        );

        // Attempt to update with weak password
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/users/" + USER_ID + "/password",
                HttpMethod.PUT,
                createAuthenticatedEntity(weakPasswordRequest),
                ProblemDetail.class
        );

        // Verify bad request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}