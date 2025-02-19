package com.bynature.application.service;

import com.bynature.domain.model.Item;
import com.bynature.domain.repository.ItemRepository;
import com.bynature.domain.service.ItemService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ItemSpringService implements ItemService {

    private final ItemRepository itemRepository;

    public ItemSpringService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public UUID createItem(Item item) {
        return itemRepository.saveItem(item);
    }

    public void updateItem(Item item) {
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.updateItem(item);
    }

    public Item getItem(UUID itemId) {
        return itemRepository.getItem(itemId);
    }

    public void deleteItem(UUID itemId) {
        itemRepository.deleteItem(itemId);
    }

    public List<Item> getAllItems() {
        return itemRepository.getAllItems();
    }

}
