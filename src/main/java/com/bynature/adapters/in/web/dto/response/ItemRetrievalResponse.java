package com.bynature.adapters.in.web.dto.response;

import com.bynature.domain.model.Item;

import java.util.UUID;

public record ItemRetrievalResponse(UUID id, String name, String description, double price, String imageUrl) {

    public static ItemRetrievalResponse fromDomain(Item item) {
        return new ItemRetrievalResponse(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getImageUrl());
    }
}
