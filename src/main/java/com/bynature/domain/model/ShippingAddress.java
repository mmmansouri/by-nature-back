package com.bynature.domain.model;

import com.bynature.domain.exception.ShippingAddressValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShippingAddress {
    private final UUID id;
    private final Customer customer;
    private String label;
    private String firstName;
    private String lastName;
    private PhoneNumber phoneNumber;
    private Email email;
    private String streetNumber;
    private String street;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShippingAddress(Customer customer, String label, String firstName, String lastName, PhoneNumber phoneNumber, Email email, String streetNumber,
                           String street, String city, String region, String postalCode, String country) {
        this.id = UUID.randomUUID();
        this.label = label;
        this.customer = customer;
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
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public ShippingAddress(UUID id, Customer customer, String label, String firstName, String lastName, PhoneNumber phoneNumber, Email email, String streetNumber,
                           String street, String city, String region, String postalCode, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.label = label;
        this.customer = customer;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getLabel() {
        return label;
    }

    public UUID getId() {
        return id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        this.validate();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.validate();
    }

    public void setEmail(Email email) {
        this.email = email;
        this.validate();
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        this.validate();
    }

    public void setStreet(String street) {
        this.street = street;
        this.validate();
    }

    public void setCity(String city) {
        this.city = city;
        this.validate();
    }

    public void setRegion(String region) {
        this.region = region;
        this.validate();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        this.validate();
    }

    public void setCountry(String country) {
        this.country = country;
        this.validate();
    }

    protected void validate() {
        record ValidationRule(Object value, String fieldName, boolean isStringField) {}

        var violations = new ArrayList<String>();

        // Using Java 21 record patterns for more elegant validation
        for (ValidationRule rule : List.of(
                new ValidationRule(customer, "Le client", false),
                new ValidationRule(id, "L'ID de l'adresse de livraison", false),
                new ValidationRule(label, "Le label", true),
                new ValidationRule(firstName, "Le prénom", true),
                new ValidationRule(lastName, "Le nom", true),
                new ValidationRule(phoneNumber, "Le numéro de téléphone", false),
                new ValidationRule(email, "L'email", false),
                new ValidationRule(streetNumber, "Le numéro de rue", true),
                new ValidationRule(street, "Le nom de rue", true),
                new ValidationRule(city, "La ville", true),
                new ValidationRule(region, "La région", true),
                new ValidationRule(postalCode, "Le code postal", true),
                new ValidationRule(country, "Le pays", true),
                new ValidationRule(createdAt, "La date de création", false),
                new ValidationRule(updatedAt, "La date de mise à jour", false)
        )) {
            if (rule.value == null) {
                violations.add("%s ne peut pas être null".formatted(rule.fieldName));
            } else if (rule.isStringField && ((String)rule.value).trim().isEmpty()) {
                violations.add("%s ne peut pas être vide".formatted(rule.fieldName));
            }
        }

        if(updatedAt !=null && createdAt!=null && updatedAt.isBefore(createdAt)) {
            violations.add("La date de mise à jour ne peut pas être avant celle de la création");
        }

        if (!violations.isEmpty()) {
            throw new ShippingAddressValidationException(violations);
        }
    }
}