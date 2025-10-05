package com.bynature.adapters.in.web.auth;

import com.bynature.application.service.AppClientSpringService;
import com.bynature.application.service.TokenService;
import com.bynature.domain.model.AppClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/auth/client")
public class AppClientAuthController {
    private static final Logger logger = LoggerFactory.getLogger(AppClientAuthController.class);


    private final AppClientSpringService appClientService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AppClientAuthController(
            AppClientSpringService appClientService,
            TokenService tokenService,
            PasswordEncoder passwordEncoder) {
        this.appClientService = appClientService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken(@RequestBody ClientAuthRequest request) {
        List<AppClient> clients = appClientService.findByAppClientId(request.clientId());

        if (clients.isEmpty()) {
            logger.debug("No client found with ID: {}", request.clientId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return clients.stream()
                .filter(client -> {
                    boolean matches = passwordEncoder.matches(request.clientSecret(), client.getAppClientSecret());
                    logger.info("Client {} authentication: active={}, passwordMatches={}",
                            client.getAppClientId(), client.isActive(), matches);
                    return client.isActive() && matches;
                })
                .map(client -> {
                    String accessToken = tokenService.generateAccessToken(client.getAppClientId(), "api");
                    String refreshToken = tokenService.generateRefreshToken(client.getAppClientId());

                    return ResponseEntity.ok(new TokenResponse(
                            accessToken,
                            refreshToken,
                            Instant.now().plus(15, ChronoUnit.MINUTES),
                            "Bearer"));
                }).findAny()
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            var jwt = tokenService.validateAndParseToken(request.refreshToken());

            if (!"refresh".equals(jwt.getClaim("type"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String clientId = jwt.getSubject();

            return appClientService.findByAppClientId(clientId).stream()
                    .filter(AppClient::isActive)
                    .map(client -> {
                        String accessToken = tokenService.generateAccessToken(client.getAppClientId(), "api");
                        String refreshToken = tokenService.generateRefreshToken(client.getAppClientId());

                        return ResponseEntity.ok(new TokenResponse(
                                accessToken,
                                refreshToken,
                                Instant.now().plus(15, ChronoUnit.MINUTES),
                                "Bearer"));
                    }).findAny()
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public record ClientAuthRequest(String clientId, String clientSecret) {}
    public record RefreshTokenRequest(String refreshToken) {}
    public record TokenResponse(String accessToken, String refreshToken, Instant expiresAt, String tokenType) {}
}
