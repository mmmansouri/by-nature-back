package com.bynature.domain.model;

import com.bynature.domain.exception.ItemValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Item {
    private final UUID id;
    private final String name;
    private final String description;
    private final double price;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Item(String name, String description, double price, String imageUrl) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt =  LocalDateTime.now();

        this.validate();
    }

    public Item(UUID id , String name, String description, double price, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.validate();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.updatedAt = now;
    }

    protected void validate() {
        List<String> violations = new ArrayList<>();
        if(id == null) {
            violations.add("Item ID cannot be null");
        }

        if(createdAt == null) {
            violations.add("Item creation date cannot be null");
        }

        if(updatedAt == null) {
            violations.add("Item update date cannot be null");
        }

        if (name == null || name.isBlank()) {
            violations.add("Item name cannot be null or empty");
        }
        if (description == null || description.isBlank()) {
            violations.add("Item description cannot be null or empty");
        }
        if (price <= 0) {
            violations.add("Item price must be greater than 0");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            violations.add("Item image URL cannot be null or empty");
        }

        if (!violations.isEmpty()) {
            throw new ItemValidationException(violations);
        }
    }
}
