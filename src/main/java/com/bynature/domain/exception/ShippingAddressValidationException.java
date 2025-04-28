package com.bynature.domain.exception;

import java.util.List;

public class ShippingAddressValidationException extends RuntimeException {
    private final List<String> violations;

    public ShippingAddressValidationException(List<String> violations) {
        super("Shipping address validation failed: " + String.join(", ", violations));
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}