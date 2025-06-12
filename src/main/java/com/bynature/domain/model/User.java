package com.bynature.domain.model;

import com.bynature.domain.exception.UserValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class User {
    private final UUID id;
    private Email email;
    private String password;  // Encrypted password
    private Customer customer;
    private boolean active;
    private Role role;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for reading from the database
    public User(UUID id, Email email, String encodedPassword, Customer customer, boolean active, Role role,
                LocalDateTime lastLoginAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = encodedPassword;
        this.customer = customer;
        this.active = active;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        validate();
    }

    // Constructor for reading from the database
    public User(UUID id, Email email, String encodedPassword, boolean active, Role role,
                LocalDateTime lastLoginAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = encodedPassword;
        this.active = active;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        validate();
    }

    // Constructor for creating a new user
    public User(Email email, String encodedPassword, Role role) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = encodedPassword;
        this.active = true;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.lastLoginAt = LocalDateTime.now();

        validate();
    }

    // Constructor for creating a new customer user
    public User(Email email, String encodedPassword, Customer customer) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = encodedPassword;
        this.active = true;
        this.role = Role.CUSTOMER;
        this.customer = customer;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.lastLoginAt = LocalDateTime.now();

        validate();
    }



    // Validation logic
    private void validate() {
        var violations = new ArrayList<String>();

        if (id == null) {
            violations.add("User ID cannot be null");
        }

        if (email == null) {
            violations.add("Email cannot be null");
        }

        if (password == null || password.isEmpty()) {
            violations.add("Password cannot be empty");
        }

        if(createdAt == null) {
            violations.add("Creation date cannot be null");
        }

        if (updatedAt == null) {
            violations.add("Updated date cannot be null");
        }

        if(this.role == null) {
            violations.add("Role cannot be null");
        }

        if(updatedAt !=null && createdAt!=null && updatedAt.isBefore(createdAt)) {
            violations.add("La date de mise à jour ne peut pas être avant celle de la création");
        }

        if (!violations.isEmpty()) {
            throw new UserValidationException(violations);
        }
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isActive() {
        return active;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void setRole(Role role) {
        this.role = role;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        validate();
    }

    public void setEmail(Email email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Link to customer
    public void linkToCustomer(Customer customer) {
        this.customer = customer;
        this.updatedAt = LocalDateTime.now();
        validate();
    }
}