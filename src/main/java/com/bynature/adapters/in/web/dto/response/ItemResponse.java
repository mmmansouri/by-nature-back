package com.bynature.adapters.in.web.dto.response;

import com.bynature.domain.model.Item;

import java.util.UUID;

public class ItemResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final double price;
    private final String imageUrl;

    public ItemResponse(UUID id, String name, String description, double price, String imageUrl) {
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

    public static ItemResponse fromDomain(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getImageUrl());
    }
}
