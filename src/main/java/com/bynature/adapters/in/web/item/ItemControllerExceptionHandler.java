package com.bynature.adapters.in.web.item;

import com.bynature.adapters.in.web.BaseExceptionHandler;
import com.bynature.domain.exception.ItemNotFoundException;
import com.bynature.domain.exception.ItemValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(assignableTypes = {ItemController.class})
public class ItemControllerExceptionHandler extends BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ItemControllerExceptionHandler.class);

    @ExceptionHandler(ItemNotFoundException.class)
    public ProblemDetail handleItemNotFound(ItemNotFoundException ex) {
        log.error("Item not found: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                HttpStatus.NOT_FOUND,
                "Item Not Found",
                ex.getMessage(),
                URI.create("https://api.bynature.com/errors/items/not-found")
        );

        if (ex.getItemId() != null) {
            problem.setProperty("itemId", ex.getItemId());
        }

        return problem;
    }

    @ExceptionHandler(ItemValidationException.class)
    public ProblemDetail handleItemValidation(ItemValidationException ex) {
        log.error("Item validation error: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Item Validation Error",
                ex.getMessage(),
                URI.create("https://api.bynature.com/errors/items/validation")
        );
        problem.setProperty("violations", ex.getViolations());

        return problem;
    }
}