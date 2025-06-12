package com.bynature.domain.model;

import com.bynature.domain.exception.CustomerValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private User user;
    private final String firstName;
    private final String lastName;
    private final Email email;
    private final PhoneNumber phoneNumber;
    private String streetNumber;

    private String street;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Customer(User user,
                    String firstName,
                    String lastName,
                    Email email,
                    PhoneNumber phoneNumber,
                    String streetNumber,
                    String street,
                    String city,
                    String region,
                    String postalCode,
                    String country) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;

        this.validate();
    }

    public Customer(UUID customerId,
                    User user,
                    String firstName,
                    String lastName,
                    Email email,
                    PhoneNumber phoneNumber,
                    LocalDateTime createdAt,
                    String streetNumber,
                    String street,
                    String city,
                    String region,
                    String postalCode,
                    String country
                    ) {
        this.user = user;
        this.id = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
        this.createdAt = createdAt;
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;


        this.validate();
    }

    public Customer(UUID customerId,
                    User user,
                    String firstName,
                    String lastName,
                    Email email,
                    PhoneNumber phoneNumber,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this.user = user;
        this.id = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;

        this.validate();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.validate();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
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

    protected void validate() {
        var violations = new ArrayList<String>();

        if (id == null) {
            violations.add("L'ID du client ne peut pas être null");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            violations.add("Le prénom ne peut pas être vide");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            violations.add("Le nom ne peut pas être vide");
        }

        if (email == null) {
            violations.add("L'email ne peut pas être null");
        }

        if (phoneNumber == null) {
            violations.add("Le numéro de téléphone ne peut pas être null");
        }

        // Validate address fields if they are set
        if (streetNumber != null && streetNumber.trim().isEmpty()) {
            violations.add("Le numéro de rue ne peut pas être vide s'il est spécifié");
        }

        if (street != null && street.trim().isEmpty()) {
            violations.add("Le nom de rue ne peut pas être vide s'il est spécifié");
        }

        if (city != null && city.trim().isEmpty()) {
            violations.add("La ville ne peut pas être vide si elle est spécifiée");
        }

        if (region != null && region.trim().isEmpty()) {
            violations.add("La région ne peut pas être vide si elle est spécifiée");
        }

        if (postalCode != null && postalCode.trim().isEmpty()) {
            violations.add("Le code postal ne peut pas être vide s'il est spécifié");
        }

        if (country != null && country.trim().isEmpty()) {
            violations.add("Le pays ne peut pas être vide s'il est spécifié");
        }
        if( createdAt == null) {
            violations.add("La date de création ne peut pas être null");
        }

        if(updatedAt !=null && createdAt!=null && updatedAt.isBefore(createdAt)) {
            violations.add("La date de mise à jour ne peut pas être avant celle de la création");
        }

        if (!violations.isEmpty()) {
            throw new CustomerValidationException(violations);
        }
    }

    // Also validate when address fields are modified
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        validateAddressField(streetNumber, "Le numéro de rue ne peut pas être vide");
    }

    public void setStreet(String street) {
        this.street = street;
        validateAddressField(street, "Le nom de rue ne peut pas être vide");
    }

    public void setCity(String city) {
        this.city = city;
        validateAddressField(city, "La ville ne peut pas être vide");
    }

    public void setRegion(String region) {
        this.region = region;
        validateAddressField(region, "La région ne peut pas être vide");
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        validateAddressField(postalCode, "Le code postal ne peut pas être vide");
    }

    public void setCountry(String country) {
        this.country = country;
        validateAddressField(country, "Le pays ne peut pas être vide");
    }

    private void validateAddressField(String field, String message) {
        if (field != null && field.trim().isEmpty()) {
            throw new CustomerValidationException(List.of(message));
        }
    }
}
