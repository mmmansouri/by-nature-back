package com.bynature.domain.exception;

import java.util.List;

public class ItemValidationException extends ByNatureValidationException  {

    public ItemValidationException(List<String> violations) {
        super(violations);
    }
}
