package com.bynature.adapters.in.web.item;

import com.bynature.adapters.in.web.exception.BaseExceptionHandler;
import com.bynature.domain.exception.ItemNotFoundException;
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

@RestControllerAdvice(assignableTypes = {ItemController.class})
@Order(1) // Higher priority than GlobalExceptionHandler
public class ItemControllerExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ItemControllerExceptionHandler.class);
    
    // Register managed exception types in static initializer
    static {
        registerHandledExceptionType(ItemNotFoundException.class);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleItemNotFound(ItemNotFoundException ex) {
        log.error("Item not found: {}", ex.getMessage());

        var problem = handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "Item Not Found",
                "items/not-found",
                exception -> ex.getItemId() != null
                        ? Map.of("itemId", ex.getItemId())
                        : Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleItemValidationException(MethodArgumentNotValidException ex) {
        return handleValidationException(
                ex,
                "Item Request Validation Failed",
                "Item request contains invalid data",
                "items/validation-error"
        );
    }
}
