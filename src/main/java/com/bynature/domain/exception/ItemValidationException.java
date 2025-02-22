package com.bynature.domain.exception;

import java.util.List;

public class ItemValidationException extends RuntimeException {
    private final List<String> violations;

    public ItemValidationException(List<String> violations) {
        super("Erreurs de validation: " + String.join(", ", violations));
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}
