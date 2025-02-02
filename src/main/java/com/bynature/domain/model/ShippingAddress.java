package com.bynature.domain.model;

public class ShippingAddress {
    private String firstName;
    private String lastName;
    private PhoneNumber phoneNumber;
    private Email email;
    private Address address;

    public ShippingAddress(Address address, String firstName, String lastName, PhoneNumber phoneNumber, Email email) {
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Address getAddress() {
        return address;
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
}
