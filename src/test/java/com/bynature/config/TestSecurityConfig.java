package com.bynature.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        // Create a JWT decoder that accepts all tokens for testing
        return token -> {
            Map<String, Object> headers = new LinkedHashMap<>();
            headers.put("alg", "none");

            Map claims = new HashMap<>();
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                try {
                    claims = new ObjectMapper().readValue(payload, Map.class);
                } catch (Exception ignored) {
                    // If parsing fails, use empty claims
                }
            }

            return new Jwt(token, Instant.now(), Instant.now().plusSeconds(3600),
                    headers, claims);
        };
    }
}