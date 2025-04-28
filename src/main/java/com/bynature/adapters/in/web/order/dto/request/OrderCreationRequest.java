package com.bynature.adapters.in.web.order.dto.request;

import com.bynature.domain.model.Email;
import com.bynature.domain.model.Order;
import com.bynature.domain.model.OrderItem;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.service.CustomerService;
import com.bynature.domain.service.ItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.UUID;

public record OrderCreationRequest( @NotNull(message = "Customer ID is required")
                                    UUID customerId,

                                    @NotEmpty(message = "Order must contain at least one item")
                                    @Valid
                                    List<OrderItemCreationRequest> orderItems,

                                    @PositiveOrZero(message = "Total must not be negative")
                                    double total,

                                    @NotNull(message = "Shipping address is required")
                                    @Valid
                                    ShippingAddressCreationRequest shippingAddress) {

    public Order toDomain(CustomerService customerService, ItemService itemService) {
        return new Order(
                customerService.getCustomer(customerId()),
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
