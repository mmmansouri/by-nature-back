package com.bynature.adapters.out.persistence.jpa.entity;

import com.bynature.domain.model.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Valid
public class CustomerEntity {

    @Id
    @NotNull(message = "Customer ID cannot be null")
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private UserEntity user;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Column(name = "phone_number", nullable = false)
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    @Column(name = "street_number")
    @NotBlank(message = "Street number number cannot be empty")
    private String streetNumber;

    @Column
    @NotBlank(message = "Street name cannot be empty")
    private String street;

    @Column
    @NotBlank(message = "City number cannot be empty")
    private String city;

    @Column
    @NotBlank(message = "Region number cannot be empty")
    private String region;

    @Column(name = "postal_code")
    @NotBlank(message = "Postal code number cannot be empty")
    private String postalCode;

    @Column
    @NotBlank(message = "Country number cannot be empty")
    private String country;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<OrderEntity> orders = new ArrayList<>();

    @Column(nullable = false)
    @NotNull(message = "Created date cannot be null")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @NotNull(message = "Updated date cannot be null")
    private LocalDateTime updatedAt;

    public CustomerEntity() {
    }

    public CustomerEntity(UUID id,
                          UserEntity user,
                          String firstName,
                          String lastName,
                          String email,
                          String phoneNumber,
                          String streetNumber,
                          String street,
                          String city,
                          String region,
                          String postalCode,
                          String country,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
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


    public static CustomerEntity fromDomain(Customer customer) {
        return new CustomerEntity(
                customer.getId(),
                UserEntity.fromDomain(customer.getUser()),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail().email(),
                customer.getPhoneNumber().number(),
                customer.getStreetNumber(),
                customer.getStreet(),
                customer.getCity(),
                customer.getRegion(),
                customer.getPostalCode(),
                customer.getCountry(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }
}
