package com.bynature.adapters.out.persistence.jpa.entity;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.ShippingAddress;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_addresses")
@Valid
public class ShippingAddressEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer cannot be null")
    private CustomerEntity customer;

    @Column(nullable = false)
    @NotBlank(message = "label name cannot be empty")
    private String label;

    @Column(nullable = false)
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Column(nullable = false)
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    @Column(nullable = false)
    @NotBlank(message = "Email cannot be empty")
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

    @Column(nullable = false)
    @NotNull(message = "Created date cannot be null")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @NotNull(message = "Updated date cannot be null")
    private LocalDateTime updatedAt;

    public ShippingAddressEntity(UUID id,
                                 CustomerEntity customer,
                                 String label,
                                 String firstName,
                                 String lastName,
                                 String phoneNumber,
                                 String email,
                                 String streetNumber,
                                 String street,
                                 String city,
                                 String region,
                                 String postalCode,
                                 String country,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {
        this.id = id;
        this.customer = customer;
        this.label = label;
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

    public ShippingAddressEntity() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public ShippingAddress toDomain() {
        return new ShippingAddress(this.id,
        this.customer.toDomain(),
        this.label,
        this.firstName,
        this.lastName,
        new PhoneNumber(this.phoneNumber),
        new Email(this.email),
        this.streetNumber,
        this.street,
        this.city,
        this.region,
        this.postalCode,
        this.country,
        this.createdAt,
        this.updatedAt);
    }
}

