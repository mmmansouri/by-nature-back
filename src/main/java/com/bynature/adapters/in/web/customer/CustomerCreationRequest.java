package com.bynature.adapters.in.web.customer;

import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CustomerCreationRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "Password must be at least 8 characters and contain at least one digit, " +
                        "one lowercase letter, one uppercase letter, one special character, and no whitespace"
        )
        String password,

        @NotBlank(message = "Email is required")
        String email,

        @NotNull(message = "Phone number is required")
        @JsonDeserialize(using = PhoneNumberDeserializer.class)
        PhoneNumber phoneNumber,

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
    public Customer toDomain(User user) {
        return new Customer(
                user,
                firstName,
                lastName,
                new Email(email),
                phoneNumber,
                streetNumber,
                street,
                city,
                region,
                postalCode,
                country
        );
    }
}