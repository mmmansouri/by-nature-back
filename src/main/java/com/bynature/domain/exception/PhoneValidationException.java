package com.bynature.domain.exception;

import java.util.List;

public class PhoneValidationException extends ByNatureValidationException {

    public PhoneValidationException(List<String> violations) {
        super(violations);
    }
}
