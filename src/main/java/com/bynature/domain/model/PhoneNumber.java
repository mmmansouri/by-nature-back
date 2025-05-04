package com.bynature.domain.model;

import com.bynature.domain.exception.PhoneValidationException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;

public record PhoneNumber(String number) {
    // Static instance of the PhoneNumberUtil for reuse.
    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    // Compact constructor for validation.
    public PhoneNumber {
        try {
            Phonenumber.PhoneNumber parsedNumber = PHONE_UTIL.parse(number, "FR");
            if (!PHONE_UTIL.isValidNumber(parsedNumber)) {
                throw new PhoneValidationException(List.of("Invalid phoneNumber: " + number));
            }
        } catch (NumberParseException e) {
            throw new PhoneValidationException(List.of("phoneNumber format error: " + e.getMessage()));
        }
    }
}
