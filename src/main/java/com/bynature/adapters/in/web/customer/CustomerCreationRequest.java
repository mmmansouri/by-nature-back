package com.bynature.adapters.in.web.customer;

import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import jakarta.validation.constraints.NotBlank;

public record CustomerCreationRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Street number number is required")
        String streetNumber,

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City number is required")
        String city,

        @NotBlank(message = "Region is required")
        String region,

        @NotBlank(message = "Postal code is required")
        String postalCode,

        @NotBlank(message = "Country is required")
        String country
) {
    public Customer toDomain() {
        Customer customer = new Customer(
                firstName,
                lastName,
                new Email(email),
                new PhoneNumber(phoneNumber)
        );

        if (streetNumber != null) customer.setStreetNumber(streetNumber);
        if (street != null) customer.setStreet(street);
        if (city != null) customer.setCity(city);
        if (region != null) customer.setRegion(region);
        if (postalCode != null) customer.setPostalCode(postalCode);
        if (country != null) customer.setCountry(country);

        return customer;
    }
}