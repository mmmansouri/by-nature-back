package com.bynature.adapters.in.web;

import com.bynature.adapters.in.web.dto.OrderRequest;
import com.bynature.adapters.in.web.dto.OrderResponse;
import com.bynature.domain.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {

        // Delegate to the use-case to create the order.
        UUID createdOrderUUID = orderService.createOrder(orderRequest.toDomain());

        // Return a 201 Created response with the location of the new order.
        return ResponseEntity
                .created(URI.create("/orders/" + createdOrderUUID))
                .body(new OrderResponse(createdOrderUUID));
    }
}