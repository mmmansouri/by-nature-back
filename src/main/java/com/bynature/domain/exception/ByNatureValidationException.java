package com.bynature.domain.exception;

import java.util.List;

public class ByNatureValidationException extends RuntimeException {
  protected final List<String> violations;

  public ByNatureValidationException(List<String> violations) {
    super("Erreurs de validation: " + String.join(", ", violations));
    this.violations = violations;
  }

  public List<String> getViolations() {
    return violations;
  }
}
