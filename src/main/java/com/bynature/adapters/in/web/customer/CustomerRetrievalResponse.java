package com.bynature.adapters.in.web.customer;

import com.bynature.domain.model.Customer;

import java.util.UUID;

public record CustomerRetrievalResponse(
        UUID id,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String streetNumber,
        String street,
        String city,
        String region,
        String postalCode,
        String country,
        String createdAt,
        String updatedAt
) {
    public static CustomerRetrievalResponse fromDomain(Customer customer) {
        return new CustomerRetrievalResponse(
                customer.getId(),
                customer.getUser().getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail().email(),
                customer.getPhoneNumber().number(),
                customer.getStreetNumber(),
                customer.getStreet(),
                customer.getCity(),
                customer.getRegion(),
                customer.getPostalCode(),
                customer.getCountry(),
                customer.getCreatedAt().toString(),
                customer.getUpdatedAt().toString()
        );
    }
}