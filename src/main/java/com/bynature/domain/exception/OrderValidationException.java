package com.bynature.domain.exception;

import java.util.List;

public class OrderValidationException extends RuntimeException {
    private final List<String> violations;

    public OrderValidationException(List<String> violations) {
        super("Erreurs de validation: " + String.join(", ", violations));
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}
