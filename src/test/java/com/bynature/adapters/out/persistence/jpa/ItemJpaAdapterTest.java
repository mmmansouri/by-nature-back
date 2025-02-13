package com.bynature.adapters.out.persistence.jpa;

import com.bynature.domain.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {ItemRepositoryAdapter.class})
public class ItemJpaAdapterTest {

    @Autowired
    private ItemRepositoryAdapter itemJpaRepository;

    @Test
    public void whenSavingItem_thenItCanBeRetrieved() {
        UUID itemId = UUID.randomUUID();
        Item item = new Item(itemId, "Test Item", "Test Description", 100.0, "http://test.com/image.jpg");

        // Save the item entity
        itemJpaRepository.saveItem(item);

        // Retrieve the item entity
        Item foundItem = itemJpaRepository.getItem(itemId);
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getName()).isEqualTo("Test Item");
    }
}