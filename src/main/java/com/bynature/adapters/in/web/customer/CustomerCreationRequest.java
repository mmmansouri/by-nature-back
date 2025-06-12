package com.bynature.adapters.in.web.customer;

import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CustomerCreationRequest(
        @NotNull(message = "User is required")
        UUID userId,

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
    public Customer toDomain(UserService userService) {
        return new Customer(
                userService.getUser(userId),
                firstName,
                lastName,
                new Email(email),
                new PhoneNumber(phoneNumber),
                streetNumber,
                street,
                city,
                region,
                postalCode,
                country
        );
    }
}