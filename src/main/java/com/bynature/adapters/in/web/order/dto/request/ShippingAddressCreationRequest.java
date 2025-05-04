package com.bynature.adapters.in.web.order.dto.request;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.ShippingAddress;
import com.bynature.domain.service.CustomerService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShippingAddressCreationRequest(@NotNull(message = "Customer ID is required")
                                             UUID customerId,

                                             @NotBlank(message = "Label name is required")
                                             String label,

                                             @NotBlank(message = "First name is required")
                                             String firstName,

                                             @NotBlank(message = "Last name is required")
                                             String lastName,

                                             @NotBlank(message = "Phone number is required")
                                             String phoneNumber,

                                             @NotBlank(message = "Email is required")
                                             String email,

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
                                             String country) {


    public ShippingAddress toDomain(CustomerService customerService) {
        return new ShippingAddress(customerService.getCustomer(customerId), label, firstName, lastName,
                new PhoneNumber(phoneNumber), new Email(email), streetNumber, street, city, region, postalCode, country);
    }

}
