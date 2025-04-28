package com.bynature.domain.exception;

import java.util.List;

public class CustomerValidationException extends ByNatureValidationException {

    public CustomerValidationException(List<String> violations) {
        super(violations);
    }

}
