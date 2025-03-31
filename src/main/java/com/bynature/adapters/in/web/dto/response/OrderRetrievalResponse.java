package com.bynature.adapters.in.web.dto.response;


import com.bynature.domain.model.Order;

import java.util.List;
import java.util.UUID;

public record OrderRetrievalResponse(UUID id, UUID customerId, List<OrderItemRetrievalResponse> orderItems,
                                     double total,
                                     String status, ShippingAddressRetrievalResponse shippingAddress) {

    public static OrderRetrievalResponse fromDomain(Order order) {
        return new OrderRetrievalResponse(order.getId(), order.getCustomerId(),
                order.getOrderItems().stream()
                        .map(model -> new OrderItemRetrievalResponse(ItemRetrievalResponse
                                .fromDomain(model.getItem()), model.getQuantity())).toList(),
                order.getTotal(),
                order.getStatus().toString(), new ShippingAddressRetrievalResponse(order.getFirstName(), order.getLastName(),
                order.getPhoneNumber().number(), order.getEmail().email(), order.getStreetNumber(), order.getStreet(),
                order.getCity(), order.getRegion(), order.getPostalCode(), order.getCountry()));
    }
}