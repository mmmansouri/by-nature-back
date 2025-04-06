package com.bynature.adapters.in.web.item;

import com.bynature.domain.model.Item;

public record ItemCreationRequest(String name, String description, double price, String imageUrl) {

    public Item toDomain() {
        return new Item(name, description, price, imageUrl);
    }
}
