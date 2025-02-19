package com.bynature.adapters.in.web.dto.response;

import com.bynature.domain.model.ShippingAddress;

public record ShippingAddressRetrievalResponse(String firstName, String lastName, String phoneNumber, String email, String streetNumber, String street, String city, String region, String postalCode, String country) {

    public static ShippingAddressRetrievalResponse fromDomain(ShippingAddress shippingAddress) {
        return new ShippingAddressRetrievalResponse(shippingAddress.getFirstName(),
                shippingAddress.getLastName(),
                shippingAddress.getPhoneNumber().number(),
                shippingAddress.getEmail().email(),
                shippingAddress.getStreetNumber(),
                shippingAddress.getStreet(),
                shippingAddress.getCity(),
                shippingAddress.getRegion(),
                shippingAddress.getPostalCode(),
                shippingAddress.getCountry());
    }
}
