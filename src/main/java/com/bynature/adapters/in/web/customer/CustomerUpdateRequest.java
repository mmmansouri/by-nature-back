package com.bynature.adapters.in.web.customer;

import com.bynature.domain.model.Customer;
import com.bynature.domain.model.PhoneNumber;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Street number is required")
        String streetNumber,

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Region is required")
        String region,

        @NotBlank(message = "Postal code is required")
        String postalCode,

        @NotBlank(message = "Country is required")
        String country
) {
    public void toDomain(Customer customer) {
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhoneNumber(new PhoneNumber(phoneNumber));
        customer.setStreetNumber(streetNumber);
        customer.setStreet(street);
        customer.setCity(city);
        customer.setRegion(region);
        customer.setPostalCode(postalCode);
        customer.setCountry(country);
    }
}