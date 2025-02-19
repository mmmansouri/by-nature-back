package com.bynature.adapters.in.web.dto.request;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.ShippingAddress;

public record ShippingAddressCreationRequest(String firstName, String lastName, String phoneNumber, String email, String streetNumber, String street, String city, String region, String postalCode, String country) {


    public ShippingAddress toDomain() {
        return new ShippingAddress(firstName, lastName, new PhoneNumber(phoneNumber), new Email(email),
                streetNumber, street, city, region, postalCode, country);
    }

}
