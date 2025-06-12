package com.bynature.adapters.in.web.user;

import com.bynature.adapters.in.web.exception.BaseExceptionHandler;
import com.bynature.domain.exception.UserNotFoundException;
import com.bynature.domain.exception.UserValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {UserController.class})
@Order(1) // Higher priority than GlobalExceptionHandler
public class UserControllerExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UserControllerExceptionHandler.class);

    // Register managed exception types in static initializer
    static {
        registerHandledExceptionType(UserNotFoundException.class);
        registerHandledExceptionType(UserValidationException.class);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "User Not Found",
                "users/not-found",
                exception -> ex.getUserId() != null
                        ? Map.of("userId", ex.getUserId())
                        : Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ProblemDetail> handleUserValidationException(UserValidationException ex) {
        log.error("User validation failed: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "User Validation Failed",
                "users/validation-error",
                exception -> Map.of("validationErrors", ex.getViolations())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return handleValidationException(
                ex,
                "User Request Validation Failed",
                "User request contains invalid data",
                "users/validation-error"
        );
    }
}
