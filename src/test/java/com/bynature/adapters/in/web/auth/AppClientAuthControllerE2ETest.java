package com.bynature.adapters.in.web.auth;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.auth.AppClientAuthController.ClientAuthRequest;
import com.bynature.adapters.in.web.auth.AppClientAuthController.RefreshTokenRequest;
import com.bynature.adapters.in.web.auth.AppClientAuthController.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppClientAuthControllerE2ETest extends AbstractByNatureTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("When authenticating with valid client credentials, should return token")
    public void whenAuthenticatingWithValidCredentials_shouldReturnToken_E2E() {
        // Create client auth request
        ClientAuthRequest validRequest = new ClientAuthRequest(
                "bynature-front", "client-secret-123"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClientAuthRequest> requestEntity = new HttpEntity<>(validRequest, headers);

        // Send request
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                "/auth/client/token",
                HttpMethod.POST,
                requestEntity,
                TokenResponse.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();
        assertThat(response.getBody().expiresAt()).isAfter(Instant.now());
        assertThat(response.getBody().tokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("When authenticating with invalid client credentials, should return unauthorized")
    public void whenAuthenticatingWithInvalidCredentials_shouldReturnUnauthorized_E2E() {
        // Create client auth request with invalid credentials
        ClientAuthRequest invalidRequest = new ClientAuthRequest(
                "bynature-front", "wrong-secret"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClientAuthRequest> requestEntity = new HttpEntity<>(invalidRequest, headers);

        // Send request
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                "/auth/client/token",
                HttpMethod.POST,
                requestEntity,
                TokenResponse.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("When refreshing with valid refresh token, should return new tokens")
    public void whenRefreshingWithValidToken_shouldReturnNewTokens_E2E() {
        // First, get a valid token
        ClientAuthRequest validRequest = new ClientAuthRequest(
                "bynature-front", "client-secret-123"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClientAuthRequest> requestEntity = new HttpEntity<>(validRequest, headers);

        ResponseEntity<TokenResponse> authResponse = restTemplate.exchange(
                "/auth/client/token",
                HttpMethod.POST,
                requestEntity,
                TokenResponse.class
        );

        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String refreshToken = authResponse.getBody().refreshToken();

        // Now try to refresh using that token
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);
        HttpEntity<RefreshTokenRequest> refreshRequestEntity = new HttpEntity<>(refreshRequest, headers);

        ResponseEntity<TokenResponse> refreshResponse = restTemplate.exchange(
                "/auth/client/refresh",
                HttpMethod.POST,
                refreshRequestEntity,
                TokenResponse.class
        );

        // Verify refresh response
        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(refreshResponse.getBody()).isNotNull();
        assertThat(refreshResponse.getBody().accessToken()).isNotBlank();
        assertThat(refreshResponse.getBody().refreshToken()).isNotBlank();
        // Check that we got new tokens
        assertThat(refreshResponse.getBody().accessToken()).isNotEqualTo(authResponse.getBody().accessToken());
    }

    @Test
    @DisplayName("When refreshing with invalid refresh token, should return unauthorized")
    public void whenRefreshingWithInvalidToken_shouldReturnUnauthorized_E2E() {
        // Try to refresh with an invalid token
        RefreshTokenRequest invalidRequest = new RefreshTokenRequest("invalid-token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RefreshTokenRequest> requestEntity = new HttpEntity<>(invalidRequest, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                "/auth/client/refresh",
                HttpMethod.POST,
                requestEntity,
                TokenResponse.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("When using access token as refresh token, should return unauthorized")
    public void whenUsingAccessTokenAsRefreshToken_shouldReturnUnauthorized_E2E() {
        // First, get valid tokens
        ClientAuthRequest validRequest = new ClientAuthRequest(
                "bynature-front", "client-secret-123"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClientAuthRequest> requestEntity = new HttpEntity<>(validRequest, headers);

        ResponseEntity<TokenResponse> authResponse = restTemplate.exchange(
                "/auth/client/token",
                HttpMethod.POST,
                requestEntity,
                TokenResponse.class
        );

        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Try to use access token for refresh
        String accessToken = authResponse.getBody().accessToken();
        RefreshTokenRequest invalidRequest = new RefreshTokenRequest(accessToken);
        HttpEntity<RefreshTokenRequest> refreshRequestEntity = new HttpEntity<>(invalidRequest, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                "/auth/client/refresh",
                HttpMethod.POST,
                refreshRequestEntity,
                TokenResponse.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("When authenticating with non-existent client ID, should return unauthorized")
    public void whenAuthenticatingWithNonExistentClientId_shouldReturnUnauthorized_E2E() {
        // Create client auth request with non-existent client ID
        ClientAuthRequest invalidRequest = new ClientAuthRequest(
                "non-existent-client", "any-secret"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClientAuthRequest> requestEntity = new HttpEntity<>(invalidRequest, headers);

        // Send request
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                "/auth/client/token",
                HttpMethod.POST,
                requestEntity,
                TokenResponse.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
