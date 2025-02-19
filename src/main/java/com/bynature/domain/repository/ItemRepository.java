package com.bynature.domain.repository;

import com.bynature.domain.model.Item;

import java.util.List;
import java.util.UUID;

public interface ItemRepository {

    UUID saveItem(Item item);

    void updateItem(Item item);

    Item getItem(UUID itemId);

    void deleteItem(UUID itemId);

    List<Item> getAllItems();
}
