package com.bynature.domain.model;

public class OrderItem {
    private final Item item;
    private final Integer quantity;

    public OrderItem(Item item, Integer quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
