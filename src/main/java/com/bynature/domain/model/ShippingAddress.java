package com.bynature.domain.model;

public class ShippingAddress {
    private final String firstName;
    private final String lastName;
    private final PhoneNumber phoneNumber;
    private final Email email;
    private final String streetNumber;
    private final String street;
    private final String city;
    private final String region;
    private final String postalCode;
    private final String country;

    public ShippingAddress(String firstName, String lastName, PhoneNumber phoneNumber, Email email, String streetNumber, String street, String city, String region, String postalCode, String country) {
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

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public Email getEmail() {
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
}
