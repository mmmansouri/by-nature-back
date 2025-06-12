package com.bynature.adapters.out.persistence.jpa.entity;

import com.bynature.domain.model.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "items")
@Valid
public class ItemEntity {

    @Id
    @NotNull(message = "Item ID cannot be null")
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Item name cannot be empty")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Item description cannot be empty")
    private String description;

    @Column(nullable = false)
    @Min(value = 0, message = "Price must be greater than 0")
    private double price;

    @Column(nullable = false)
    @NotBlank(message = "Image URL cannot be empty")
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "Created date cannot be null")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull(message = "Updated date cannot be null")
    private LocalDateTime updatedAt;

    public ItemEntity(UUID id, String name, String description, double price, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ItemEntity() {

    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Item toDomain() {
        return new Item(id, name, description, price, imageUrl, createdAt, updatedAt);
    }


}
