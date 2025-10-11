package com.bynature.adapters.in.web.customer;

import com.bynature.domain.model.PhoneNumber;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PhoneNumberDeserializer extends JsonDeserializer<PhoneNumber> {
    @Override
    public PhoneNumber deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return new PhoneNumber(value);
    }
}
