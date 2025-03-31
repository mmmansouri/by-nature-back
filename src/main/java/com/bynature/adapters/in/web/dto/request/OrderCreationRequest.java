package com.bynature.adapters.in.web.dto.request;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderItem;
import com.bynature.domain.model.OrderStatus;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.service.ItemService;

import java.util.List;
import java.util.UUID;

public record OrderCreationRequest( UUID customerId,
                                    List<OrderItemCreationRequest> orderItems,
                                    double total,
                                    OrderStatus status,
                                    ShippingAddressCreationRequest shippingAddress) {

    public Order toDomain(ItemService itemService) {
        return new Order(
                customerId(),
                orderItems().stream()
                        .map(orderItem -> new OrderItem(
                                itemService.getItem(orderItem.itemId()),
                                orderItem.quantity()))
                        .toList(),
                total(),
                shippingAddress().firstName(),
                shippingAddress().lastName(),
                new PhoneNumber(shippingAddress().phoneNumber()),
                new Email(shippingAddress().email()),
                shippingAddress().streetNumber(),
                shippingAddress().street(),
                shippingAddress().city(),
                shippingAddress().region(),
                shippingAddress().postalCode(),
                shippingAddress().country()
        );
    }
}
