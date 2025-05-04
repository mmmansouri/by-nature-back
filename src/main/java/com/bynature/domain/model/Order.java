package com.bynature.domain.model;

import com.bynature.domain.exception.OrderValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final Customer customer;
    private final List<OrderItem> orderItems;
    private final double total;
    private OrderStatus status;
    private String paymentIntentId;
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


    public Order(Customer customer, List<OrderItem> orderItems, double total, String firstName,
                 String lastName, PhoneNumber phoneNumber, Email email, String streetNumber, String street, String city,
                 String region, String postalCode, String country) {
        this.id = UUID.randomUUID();
        this.customer = customer;
        this.orderItems = orderItems;
        this.total = total;
        this.status = OrderStatus.CREATED;
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

    public Order(UUID id, Customer customer, List<OrderItem> orderItems, double total, OrderStatus status, String firstName,
                 String lastName, PhoneNumber phoneNumber, Email email, String streetNumber, String street, String city,
                 String region, String postalCode, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customer = customer;
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

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
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
        this.validate();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.validate();
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        this.updatedAt = LocalDateTime.now();
        this.validate();
    }

    protected void validate() {
        List<String> violations = new ArrayList<>();

        if (id == null) {
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

        if (customer == null) {
            violations.add("L'ID du client ne peut pas être null");
        }

        if (orderItems == null || orderItems.isEmpty()) {
            violations.add("La liste des articles ne peut pas être vide");
        }

        if (total <= 0) {
            violations.add("Le total doit être positif");
        }

        if (status == null) {
            violations.add("Le statut ne peut pas être vide");
        }

        if( createdAt == null) {
            violations.add("La date de création ne peut pas être null");
        }

        if(updatedAt !=null && createdAt!=null && updatedAt.isBefore(createdAt)) {
            violations.add("La date de mise à jour ne peut pas être avant celle de la création");
        }


        if (!violations.isEmpty()) {
            throw new OrderValidationException(violations);
        }
    }

}
