package com.bynature.adapters.in.web.user;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.user.dto.UserCreationRequest;
import com.bynature.adapters.in.web.user.dto.UserEmailUpdateRequest;
import com.bynature.adapters.in.web.user.dto.UserPasswordUpdateRequest;
import com.bynature.adapters.in.web.user.dto.UserRetrievalResponse;
import com.bynature.domain.model.Role;
import com.bynature.domain.model.User;
import com.bynature.domain.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("When get existing user, should return user - E2E")
    public void whenGetExistingUser_shouldReturnUser_E2E() {
        User user = userService.getUser(USER_ID);

        ResponseEntity<UserRetrievalResponse> response = restTemplate.getForEntity(
                "/users/" + USER_ID,
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
        ResponseEntity<UserRetrievalResponse> response = restTemplate.getForEntity(
                "/users/" + UUID.randomUUID(),
                UserRetrievalResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("When create valid user, should return created - E2E")
    public void whenCreateValidUser_shouldReturnCreated_E2E() {
        UserCreationRequest validRequest = new UserCreationRequest(
                "new.user@example.com",
                "P@ssw0rd1",
                Role.CUSTOMER
        );

        ResponseEntity<UUID> response = restTemplate.postForEntity(
                "/users",
                validRequest,
                UUID.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();

        UUID userId = response.getBody();
        ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.getForEntity(
                "/users/" + userId,
                UserRetrievalResponse.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserRetrievalResponse user = getResponse.getBody();
        assertThat(user).isNotNull();
        assertThat(user.email()).isEqualTo(validRequest.email());
        assertThat(user.role()).isEqualTo(validRequest.role().name());
        assertThat(user.active()).isTrue();
    }

    @Test
    @DisplayName("Should successfully activate a user")
    public void whenActivateUser_shouldUpdateActiveStatusToTrue() {
        // First deactivate the user
        restTemplate.put(
                "/users/" + USER_ID + "/deactivate",
                null
        );

        // Then activate the user
        ResponseEntity<Void> response = restTemplate.exchange(
                "/users/" + USER_ID + "/activate",
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify the user was activated
        ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.getForEntity(
                "/users/" + USER_ID,
                UserRetrievalResponse.class
        );

        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().active()).isTrue();
    }

    @Test
    @DisplayName("Should successfully deactivate a user")
    public void whenDeactivateUser_shouldUpdateActiveStatusToFalse() {
        // First activate the user
        restTemplate.put(
                "/users/" + USER_ID + "/activate",
                null
        );

        // Then deactivate the user
        ResponseEntity<Void> response = restTemplate.exchange(
                "/users/" + USER_ID + "/deactivate",
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify the user was deactivated
        ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.getForEntity(
                "/users/" + USER_ID,
                UserRetrievalResponse.class
        );

        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().active()).isFalse();
    }

    private static Stream<Arguments> invalidUserRequests() {
        return Stream.of(
                Arguments.of("Empty email",
                        new UserCreationRequest("", "P@ssw0rd1", Role.CUSTOMER),
                        "email", "Email is required"),
                Arguments.of("Invalid email format",
                        new UserCreationRequest("not-an-email", "P@ssw0rd1", Role.CUSTOMER),
                        "email", "Invalid email format"),
                Arguments.of("Empty password",
                        new UserCreationRequest("user@example.com", "", Role.CUSTOMER),
                        "password", "Password is required"),
                Arguments.of("Weak password - no special character",
                        new UserCreationRequest("user@example.com", "Password123", Role.CUSTOMER),
                        "password", "Password must be at least 8 characters"),
                Arguments.of("Weak password - no uppercase",
                        new UserCreationRequest("user@example.com", "password@123", Role.CUSTOMER),
                        "password", "Password must be at least 8 characters"),
                Arguments.of("Weak password - no lowercase",
                        new UserCreationRequest("user@example.com", "PASSWORD@123", Role.CUSTOMER),
                        "password", "Password must be at least 8 characters"),
                Arguments.of("Weak password - no digit",
                        new UserCreationRequest("user@example.com", "Password@ABC", Role.CUSTOMER),
                        "password", "Password must be at least 8 characters"),
                Arguments.of("Null role",
                        new UserCreationRequest("user@example.com", "P@ssw0rd1", null),
                        "role", "Role is required")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUserRequests")
    @DisplayName("Should validate UserCreationRequest fields")
    void shouldValidateUserCreationRequestFields(String testName,
                                                 UserCreationRequest invalidRequest,
                                                 String fieldName,
                                                 String expectedErrorMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserCreationRequest> requestEntity = new HttpEntity<>(invalidRequest, headers);

        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity(
                "/users",
                requestEntity,
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
                "P@ssw0rd1",
                Role.ADMIN
        );

        User user = validRequest.toDomain();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail().email()).isEqualTo("test.user@example.com");
        assertThat(user.getPassword()).isEqualTo("P@ssw0rd1");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should successfully update user email")
    public void whenUpdateUserEmail_shouldUpdateEmail() {
        // Create email update request
        UserEmailUpdateRequest updateRequest = new UserEmailUpdateRequest("updated.email@example.com");

        // Update the user's email
        HttpEntity<UserEmailUpdateRequest> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<Void> response = restTemplate.exchange(
                "/users/" + USER_ID + "/email",
                HttpMethod.PATCH,
                requestEntity,
                Void.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify the email was updated
        ResponseEntity<UserRetrievalResponse> getResponse = restTemplate.getForEntity(
                "/users/" + USER_ID,
                UserRetrievalResponse.class
        );

        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().email()).isEqualTo("updated.email@example.com");
    }

    @Test
    @DisplayName("Should reject invalid email format")
    public void whenUpdateUserWithInvalidEmail_shouldReturnBadRequest() {
        // Create invalid email update request
        UserEmailUpdateRequest invalidRequest = new UserEmailUpdateRequest("invalid-email");

        // Attempt to update with invalid email
        HttpEntity<UserEmailUpdateRequest> requestEntity = new HttpEntity<>(invalidRequest);
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/users/" + USER_ID + "/email",
                HttpMethod.PATCH,
                requestEntity,
                ProblemDetail.class
        );

        // Verify bad request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should successfully update user password")
    public void whenUpdateUserPassword_shouldUpdatePassword() {
        // Create password update request
        UserPasswordUpdateRequest updateRequest = new UserPasswordUpdateRequest(
                "OldP@ssw0rd",
                "NewP@ssw0rd123"
        );

        // Update the user's password
        HttpEntity<UserPasswordUpdateRequest> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<Void> response = restTemplate.exchange(
                "/users/" + USER_ID + "/password",
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
        HttpEntity<UserPasswordUpdateRequest> requestEntity = new HttpEntity<>(weakPasswordRequest);
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/users/" + USER_ID + "/password",
                HttpMethod.PUT,
                requestEntity,
                ProblemDetail.class
        );

        // Verify bad request response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}