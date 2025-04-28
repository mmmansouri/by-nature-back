package com.bynature.domain.model;

import com.bynature.domain.exception.ShippingAddressValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShippingAddress {
    private final UUID id;
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
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShippingAddress(String firstName, String lastName, PhoneNumber phoneNumber, Email email, String streetNumber,
                           String street, String city, String region, String postalCode, String country) {
        this.id = UUID.randomUUID();
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

    public ShippingAddress(UUID id, String firstName, String lastName, PhoneNumber phoneNumber, Email email, String streetNumber,
                           String street, String city, String region, String postalCode, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
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
    }

    protected void validate() {
        record ValidationRule(Object value, String fieldName, boolean isStringField) {}

        var violations = new ArrayList<String>();

        // Using Java 21 record patterns for more elegant validation
        for (ValidationRule rule : List.of(
                new ValidationRule(id, "L'ID de l'adresse de livraison", false),
                new ValidationRule(firstName, "Le prénom", true),
                new ValidationRule(lastName, "Le nom", true),
                new ValidationRule(phoneNumber, "Le numéro de téléphone", false),
                new ValidationRule(email, "L'email", false),
                new ValidationRule(streetNumber, "Le numéro de rue", true),
                new ValidationRule(street, "Le nom de rue", true),
                new ValidationRule(city, "La ville", true),
                new ValidationRule(region, "La région", true),
                new ValidationRule(postalCode, "Le code postal", true),
                new ValidationRule(country, "Le pays", true)
        )) {
            if (rule.value == null) {
                violations.add("%s ne peut pas être null".formatted(rule.fieldName));
            } else if (rule.isStringField && ((String)rule.value).trim().isEmpty()) {
                violations.add("%s ne peut pas être vide".formatted(rule.fieldName));
            }
        }

        if (!violations.isEmpty()) {
            throw new ShippingAddressValidationException(violations);
        }
    }
}