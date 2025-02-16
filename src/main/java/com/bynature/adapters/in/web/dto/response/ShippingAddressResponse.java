package com.bynature.adapters.in.web.dto.response;

import com.bynature.domain.model.ShippingAddress;

public class ShippingAddressResponse {
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String email;
    private final String streetNumber;
    private final String street;
    private final String city;
    private final String region;
    private final String postalCode;
    private final String country;

    public ShippingAddressResponse(String firstName, String lastName, String phoneNumber, String email, String streetNumber, String street, String city, String region, String postalCode, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public static ShippingAddressResponse fromDomain(ShippingAddress shippingAddress) {
        return new ShippingAddressResponse(shippingAddress.getFirstName(),
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
