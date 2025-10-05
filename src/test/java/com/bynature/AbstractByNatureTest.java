package com.bynature;

import com.bynature.adapters.in.web.auth.dto.AuthResponse;
import com.bynature.adapters.in.web.auth.dto.LoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class AbstractByNatureTest {


    @Autowired
    protected TestRestTemplate restTemplate;

    protected String accessToken;

    /**
     * Obtains an access token for the specified user credentials
     */
    protected String obtainAccessToken(String username, String password) {
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        LoginRequest loginRequest = new LoginRequest(username, password);
        ResponseEntity<AuthResponse> loginResponse = restTemplate.exchange(
                "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest, loginHeaders),
                AuthResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return loginResponse.getBody().token();
    }

    /**
     * Creates HTTP headers with a Bearer authentication token
     */
    protected HttpHeaders createAuthenticatedHeaders() {
        if (accessToken == null) {
            throw new IllegalStateException("Access token not initialized. Call authenticateUser() first");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    /**
     * Authenticates as the default test user
     */
    protected void authenticateUser() {
        accessToken = obtainAccessToken("john.doe@example.com", "Str0ngP@ssword123!");
    }

    /**
     * Authenticates as a specific user
     */
    protected void authenticateUser(String username, String password) {
        accessToken =  obtainAccessToken(username, password);
    }

    /**
     * Creates an authenticated HttpEntity with no body (for GET requests)
     */
    protected <T> HttpEntity<T> createAuthenticatedEntity() {
        return new HttpEntity<>(createAuthenticatedHeaders());
    }

    /**
     * Creates an authenticated HttpEntity with a request body
     */
    protected <T> HttpEntity<T> createAuthenticatedEntity(T body) {
        return new HttpEntity<>(body, createAuthenticatedHeaders());
    }
}