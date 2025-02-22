package com.bynature.domain.model;

import com.bynature.domain.exception.OrderValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID customerId;
    private final Map<UUID, Integer> orderItems;
    private final double total;
    private final String status;
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


   public Order(UUID customerId, Map<UUID, Integer> orderItems, double total, String status, String firstName, String lastName, PhoneNumber phoneNumber, Email email, String streetNumber, String street, String city, String region, String postalCode, String country) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.total = total;
        this.status = status;
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

       this.validate();
   }

    public Order(UUID id, UUID customerId, Map<UUID, Integer> orderItems, double total, String status, String firstName, String lastName, PhoneNumber phoneNumber, Email email, String streetNumber, String street, String city, String region, String postalCode, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.total = total;
        this.status = status;
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

        this.validate();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Map<UUID, Integer> getOrderItems() {
        return orderItems;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
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
        List<String> violations = new ArrayList<>();

        if(id == null) {
            violations.add("L'ID de la commande ne peut pas être null");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            violations.add("Le prénom ne peut pas être vide");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            violations.add("Le nom ne peut pas être vide");
        }

        if (phoneNumber == null) {
            violations.add("Le numéro de téléphone ne peut pas être null");
        }

        if (email == null) {
            violations.add("L'email ne peut pas être null");
        }

        if (streetNumber == null || streetNumber.trim().isEmpty()) {
            violations.add("Le numéro de rue ne peut pas être vide");
        }

        if (street == null || street.trim().isEmpty()) {
            violations.add("Le nom de rue ne peut pas être vide");
        }

        if (city == null || city.trim().isEmpty()) {
            violations.add("La ville ne peut pas être vide");
        }

        if (region == null || region.trim().isEmpty()) {
            violations.add("La région ne peut pas être vide");
        }

        if (postalCode == null || postalCode.trim().isEmpty()) {
            violations.add("Le code postal ne peut pas être vide");
        }

        if (country == null || country.trim().isEmpty()) {
            violations.add("Le pays ne peut pas être vide");
        }

        if (customerId == null) {
            violations.add("L'ID du client ne peut pas être null");
        }

        if (orderItems == null || orderItems.isEmpty()) {
            violations.add("La liste des articles ne peut pas être vide");
        }

        if (total <= 0) {
            violations.add("Le total doit être positif");
        }

        if (status == null || status.trim().isEmpty()) {
            violations.add("Le statut ne peut pas être vide");
        }

        if (!violations.isEmpty()) {
            throw new OrderValidationException(violations);
        }
    }

}
