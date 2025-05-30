package com.bynature.adapters.in.web.order.dto.response;

import com.bynature.adapters.in.web.item.ItemRetrievalResponse;
import com.bynature.domain.model.Item;

public record OrderItemRetrievalResponse(ItemRetrievalResponse item, Integer quantity) {

    public static OrderItemRetrievalResponse fromDomain(Item item, Integer quantity) {
        return new OrderItemRetrievalResponse(ItemRetrievalResponse.fromDomain(item), quantity);
    }
}
