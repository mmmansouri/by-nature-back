package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.entity.ItemEntity;
import com.bynature.adapters.out.persistence.jpa.repository.ItemJpaRepository;
import com.bynature.domain.model.Item;
import com.bynature.domain.repository.ItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryAdapter implements ItemRepository {
    private final ItemJpaRepository itemJpaRepository;

    public ItemRepositoryAdapter(ItemJpaRepository itemJpaRepository) {
        this.itemJpaRepository = itemJpaRepository;
    }

    @Override
    public UUID saveItem(Item item) {
        // Map the domain Item to a persistence ItemEntity
        ItemEntity entity = mapToEntity(item);
        // Save using Spring Data JPA repository
        ItemEntity savedEntity = itemJpaRepository.save(entity);
        return savedEntity.getId();
    }

    @Override
    public void updateItem(Item item) {
        // For update, perform the mapping and let JPA handle the merge/update
        ItemEntity entity = mapToEntity(item);
        itemJpaRepository.save(entity);
    }

    @Override
    public Item getItem(UUID itemId) {
        // Retrieve the entity and map back to the domain model
        Optional<ItemEntity> optionalEntity = itemJpaRepository.findById(itemId);
        return optionalEntity.map(this::mapToDomain).orElse(null);
    }

    @Override
    public List<Item> getAllItems() {
        return itemJpaRepository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(UUID itemId) {
        itemJpaRepository.deleteById(itemId);
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
