package com.bynature.domain.model;

import java.time.LocalDateTime;
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
    private final LocalDateTime updatedAt;


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
}
