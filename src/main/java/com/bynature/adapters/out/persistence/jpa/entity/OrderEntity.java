package com.bynature.adapters.out.persistence.jpa.entity;


import com.bynature.domain.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Valid
public class OrderEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer cannot be null")
    private CustomerEntity customer;

    @Column(nullable = false)
    @Min(value = 0, message = "Total must be greater than 0")
    private double total;

    @Enumerated(EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    @NotNull(message = "Order status cannot be null")
    private OrderStatus status;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(nullable = false)
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Last name name cannot be empty")
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "Phone number name cannot be empty")
    private String phoneNumber;

    @Column(nullable = false)
    @NotBlank(message = "Email name cannot be empty")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Street number name cannot be empty")
    private String streetNumber;

    @Column(nullable = false)
    @NotBlank(message = "Street name cannot be empty")
    private String street;

    @Column(nullable = false)
    @NotBlank(message = "City name cannot be empty")
    private String city;

    @Column(nullable = false)
    @NotBlank(message = "Region name cannot be empty")
    private String region;

    @Column(nullable = false)
    @NotBlank(message = "Postal code name cannot be empty")
    private String postalCode;

    @Column(nullable = false)
    @NotBlank(message = "Country name cannot be empty")
    private String country;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @Column(nullable = false)
    @NotNull(message = "Created date cannot be null")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @NotNull(message = "Updated date cannot be null")
    private LocalDateTime updatedAt;

    public OrderEntity(UUID id, CustomerEntity customer, double total, OrderStatus status, String firstName, String lastName, String phoneNumber, String email, String streetNumber, String street, String city, String region, String postalCode, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customer = customer;
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

    public OrderEntity() {

    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper to add order items
    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // Helper to remove order items
    public void removeOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

}
