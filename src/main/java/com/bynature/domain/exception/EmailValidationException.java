package com.bynature.domain.exception;

import java.util.List;

public class EmailValidationException extends ByNatureValidationException {

    public EmailValidationException(List<String> violations) {
        super(violations);
    }
}
