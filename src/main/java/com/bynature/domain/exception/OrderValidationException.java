package com.bynature.domain.exception;

import java.util.List;

public class OrderValidationException extends ByNatureValidationException  {

    public OrderValidationException(List<String> violations) {
        super(violations);
    }
}
