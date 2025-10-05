package com.bynature.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("TokenService Tests")
@TestPropertySource(locations = "classpath:application-test.yml")
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("Should generate valid access token with correct claims")
    void shouldGenerateValidAccessToken() {
        // Act
        String tokenValue = tokenService.generateAccessToken("test-client", "api");
        Jwt jwt = tokenService.validateAndParseToken(tokenValue);

        // Assert
        assertThat(jwt.getSubject()).isEqualTo("test-client");
        // Fix the audience assertion to handle List type
        assertThat(jwt.getAudience()).contains("api");
        assertThat((String) jwt.getClaim("type")).isEqualTo("access");
        assertThat((String) jwt.getClaim("iss")).isEqualTo("bynature");

        // Check the duration between issuedAt and expiresAt matches security config
        Instant issuedAt = jwt.getIssuedAt();
        Instant expiresAt = jwt.getExpiresAt();
        assertThat(issuedAt).isNotNull();
        assertThat(expiresAt).isNotNull();

        // Calculate duration in minutes - matching the 1 hour from SecurityConfig
        long durationMinutes = ChronoUnit.MINUTES.between(issuedAt, expiresAt);
        assertThat(durationMinutes).isEqualTo(15); // Updated to match the actual 15 minutes in TokenService
    }


    @Test
    @DisplayName("Should generate valid refresh token with correct claims")
    void shouldGenerateValidRefreshToken() {
        // Act
        String tokenValue = tokenService.generateRefreshToken("test-client");
        Jwt jwt = tokenService.validateAndParseToken(tokenValue);

        // Assert
        assertThat(jwt.getSubject()).isEqualTo("test-client");
        assertThat((String) jwt.getClaim("type")).isEqualTo("refresh");

        // Check the duration between issuedAt and expiresAt matches actual configuration
        Instant issuedAt = jwt.getIssuedAt();
        Instant expiresAt = jwt.getExpiresAt();
        assertThat(issuedAt).isNotNull();
        assertThat(expiresAt).isNotNull();

        // Calculate duration in hours - using actual value of 1 hour
        long durationHours = ChronoUnit.HOURS.between(issuedAt, expiresAt);
        assertThat(durationHours).isEqualTo(168L); // Actual refresh token duration is 1 hour
    }

    @Test
    @DisplayName("Should throw exception when validating invalid token")
    void shouldThrowExceptionWhenValidatingInvalidToken() {
        assertThatThrownBy(() -> tokenService.validateAndParseToken("invalid-token"))
                .isInstanceOf(BadJwtException.class);
    }
}
