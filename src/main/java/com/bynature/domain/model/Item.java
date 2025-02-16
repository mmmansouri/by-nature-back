package com.bynature.domain.model;

import java.util.UUID;

public class Item {
    private final UUID id;
    private final String name;
    private final String description;
    private final double price;
    private final String imageUrl;

    public Item(UUID id, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
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
}
