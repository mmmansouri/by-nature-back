package com.bynature.adapters.in.web.auth;

import com.bynature.adapters.in.web.exception.BaseExceptionHandler;
import com.bynature.domain.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {OAuth2LoginController.class})
@Order(1)
public class AuthControllerExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerExceptionHandler.class);

    static {
        registerHandledExceptionType(AuthenticationException.class);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication failed: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "auth/unauthorized",
                exception -> Map.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return handleValidationException(
                ex,
                "Authentication Request Validation Failed",
                "Authentication request contains invalid data",
                "auth/validation-error"
        );
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationServiceException.class})
    public ResponseEntity<ProblemDetail> handleSpringSecurityAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        log.error("Spring Security authentication failed: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "auth/unauthorized",
                exception -> Map.of("message", "Invalid email or password")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }
}