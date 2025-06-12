package com.bynature.domain.exception;

import java.util.List;

public class UserValidationException extends ByNatureValidationException {

    public UserValidationException(List<String> violations) {
        super(violations);
    }

}
