package com.bynature.adapters.in.web.exception;

import com.bynature.domain.exception.ByNatureValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.StringJoiner;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE) // Ensure this runs last
public class GlobalExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        // Check if this exception should be handled by a more specific handler
        if (isExceptionTypeHandled(ex.getClass())) {
            log.debug("Exception {} is handled by a more specific handler, skipping global handler",
                    ex.getClass().getSimpleName());
            // Return null to indicate this handler should be skipped
            // Spring will continue searching for other handlers
            return null;
        }
        
        // Get the most specific detail from exception hierarchy
        StringJoiner detailCollector = new StringJoiner(", ");
        Throwable current = ex;

        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                detailCollector.add(current.getMessage());
            }
            current = current.getCause();
        }

        String detail = detailCollector.length() > 0
                ? detailCollector.toString()
                : "An unexpected error occurred";

        var problemDetail = createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                detail,
                "server-error"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(ByNatureValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidation(ByNatureValidationException ex) {
        log.error("Data validation error: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Order Validation Error",
                "orders/validation",
                exception -> Map.of("validationErrors", ex.getViolations())
        );

        return ResponseEntity.badRequest().body(problem);
    }
}
