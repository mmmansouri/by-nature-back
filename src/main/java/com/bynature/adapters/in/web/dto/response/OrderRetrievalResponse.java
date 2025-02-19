package com.bynature.adapters.in.web.dto.response;


import com.bynature.domain.model.Order;

import java.util.Map;
import java.util.UUID;

public record OrderRetrievalResponse(UUID id, UUID customerId, Map<UUID, Integer> orderItems, double total,
                                     String status, ShippingAddressRetrievalResponse shippingAddress) {

    public static OrderRetrievalResponse fromDomain(Order order) {
        return new OrderRetrievalResponse(order.getId(), order.getCustomerId(), order.getOrderItems(), order.getTotal(),
                order.getStatus(), new ShippingAddressRetrievalResponse(order.getFirstName(), order.getLastName(),
                order.getPhoneNumber().number(), order.getEmail().email(), order.getStreetNumber(), order.getStreet(),
                order.getCity(), order.getRegion(), order.getPostalCode(), order.getCountry()));
    }
}
