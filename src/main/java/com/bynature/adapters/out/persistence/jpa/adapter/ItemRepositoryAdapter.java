package com.bynature.adapters.out.persistence.jpa.adapter;

import com.bynature.adapters.out.persistence.jpa.entity.ItemEntity;
import com.bynature.adapters.out.persistence.jpa.repository.ItemJpaRepository;
import com.bynature.domain.exception.ItemNotFoundException;
import com.bynature.domain.model.Item;
import com.bynature.domain.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryAdapter implements ItemRepository {

    private static final Logger log = LoggerFactory.getLogger(ItemRepositoryAdapter.class);

    private final ItemJpaRepository itemJpaRepository;

    public ItemRepositoryAdapter(ItemJpaRepository itemJpaRepository) {
        this.itemJpaRepository = itemJpaRepository;
    }

    @Override
    public UUID saveItem(Item item) {
        log.debug("Saving item with ID: {} for status", item.getId());

        // Map the domain Item to a persistence ItemEntity
        ItemEntity entity = mapToEntity(item);
        // Save using Spring Data JPA repository
        ItemEntity savedEntity = itemJpaRepository.save(entity);

        log.info("Item item with ID: {} for status", item.getId());

        return savedEntity.getId();
    }

    @Override
    public void updateItem(Item item) {

        log.debug("Updating item with ID: {} for status", item.getId());
        // For update, perform the mapping and let JPA handle the merge/update
        ItemEntity entity = mapToEntity(item);
        itemJpaRepository.save(entity);

        log.info("Item updated with ID: {} for status", item.getId());
    }

    @Override
    public Item getItem(UUID itemId) {
        log.debug("Fetching item with ID: {}", itemId);
        // Retrieve the entity and map back to the domain model
        Optional<ItemEntity> optionalEntity = itemJpaRepository.findById(itemId);
        return optionalEntity.map(this::mapToDomain)
                .orElseThrow(()->new ItemNotFoundException("Item not found with id: " + itemId));
    }

    @Override
    public List<Item> getAllItems() {
        log.debug("Fetching all items");
        return itemJpaRepository.findAll()
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(UUID itemId) {
        log.debug("Deleting item with ID: {}", itemId);

        itemJpaRepository.deleteById(itemId);

        log.info("Customer deleted with ID: {}", itemId);
    }

    // Mapping from domain to persistence entity
    private ItemEntity mapToEntity(Item item) {
        return new ItemEntity(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getImageUrl(),
                item.getCreatedAt(), item.getUpdatedAt());
    }

    // Mapping from persistence entity to domain model
    private Item mapToDomain(ItemEntity entity) {
        return entity.toDomain();
    }
}
