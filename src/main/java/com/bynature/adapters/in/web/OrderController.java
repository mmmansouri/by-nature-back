package com.bynature.adapters.in.web;

import com.bynature.adapters.in.web.dto.request.OrderCreationRequest;
import com.bynature.adapters.in.web.dto.response.OrderRetrievalResponse;
import com.bynature.domain.model.Order;
import com.bynature.domain.service.ItemService;
import com.bynature.domain.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ItemService itemService;

    public OrderController(OrderService orderService, ItemService itemService) {
        this.orderService = orderService;
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<OrderRetrievalResponse> createOrder(@RequestBody OrderCreationRequest orderCreationRequest) {

        UUID createdOrderUUID = orderService.createOrder(orderCreationRequest.toDomain(itemService));

        OrderRetrievalResponse orderRetrievalResponse = OrderRetrievalResponse
                .fromDomain(orderService.getOrder(createdOrderUUID));

        // Return a 201 Created response with the location of the new order.
        return ResponseEntity
                .created(URI.create("/orders/" + orderRetrievalResponse.id()))
                .body(orderRetrievalResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRetrievalResponse> getOrder(@PathVariable("id") UUID uuid) {

        Order order = orderService.getOrder(uuid);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .body(OrderRetrievalResponse.fromDomain(order));
    }
}