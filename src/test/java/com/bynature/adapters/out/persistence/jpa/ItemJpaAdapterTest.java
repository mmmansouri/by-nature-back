package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.adapter.ItemRepositoryAdapter;
import com.bynature.domain.exception.ItemNotFoundException;
import com.bynature.domain.exception.ItemValidationException;
import com.bynature.domain.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ContextConfiguration(classes = {ItemRepositoryAdapter.class})
@DisplayName("Item JPA Adapter Tests")
public class ItemJpaAdapterTest extends AbstractJpaTest {

    @Autowired
    private ItemRepositoryAdapter itemJpaRepository;

    @Test
    @DisplayName("When saving an item, then it can be retrieved")
    public void whenSavingItem_thenItCanBeRetrieved() {
        Item item = new Item( "Test Item", "Test Description", 100.0,
                "http://test.com/image.jpg");

        // Save the item entity
        itemJpaRepository.saveItem(item);

        // Retrieve the item entity
        Item foundItem = itemJpaRepository.getItem(item.getId());
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getName()).isEqualTo("Test Item");
    }
    
    @Test
    @DisplayName("When updating an item, then changes are persisted")
    public void whenUpdatingItem_thenChangesArePersisted() {
        // Arrange
        Item item = new Item("Original Item", "Original Description", 100.0,
                "http://test.com/image.jpg");
        itemJpaRepository.saveItem(item);
        
        // Create an updated version of the item with the same ID
        Item updatedItem = new Item(item.getId(), "Updated Item", "Updated Description", 
                150.0, "http://test.com/updated.jpg", item.getCreatedAt(), LocalDateTime.now());
        
        // Act
        itemJpaRepository.updateItem(updatedItem);
        
        // Assert
        Item retrievedItem = itemJpaRepository.getItem(item.getId());
        assertThat(retrievedItem.getName()).isEqualTo("Updated Item");
        assertThat(retrievedItem.getDescription()).isEqualTo("Updated Description");
        assertThat(retrievedItem.getPrice()).isEqualTo(150.0);
        assertThat(retrievedItem.getImageUrl()).isEqualTo("http://test.com/updated.jpg");
        // CreatedAt should remain unchanged
        assertThat(retrievedItem.getCreatedAt()).isEqualTo(item.getCreatedAt());
        // UpdatedAt should be different
        assertThat(retrievedItem.getUpdatedAt()).isAfter(item.getCreatedAt());
    }
    
    @Test
    @DisplayName("When getting all items, then all saved items are returned")
    public void whenGettingAllItems_thenAllSavedItemsAreReturned() {
        // Arrange
        Item item1 = new Item("Item 1", "Description 1", 100.0, "http://test.com/1.jpg");
        Item item2 = new Item("Item 2", "Description 2", 200.0, "http://test.com/2.jpg");
        Item item3 = new Item("Item 3", "Description 3", 300.0, "http://test.com/3.jpg");
        
        itemJpaRepository.saveItem(item1);
        itemJpaRepository.saveItem(item2);
        itemJpaRepository.saveItem(item3);
        
        // Act
        List<Item> items = itemJpaRepository.getAllItems();
        
        // Assert
        assertThat(items).hasSize(13);
        assertThat(items).extracting(Item::getName)
                .containsAnyElementsOf(List.of("Item 1", "Item 2", "Item 3"));
    }
    
    @Test
    @DisplayName("When deleting an item, then it can no longer be retrieved")
    public void whenDeletingItem_thenItCanNoLongerBeRetrieved() {
        // Arrange
        Item item = new Item("Item to Delete", "Will be deleted", 100.0, "http://test.com/delete.jpg");
        itemJpaRepository.saveItem(item);
        
        // Verify item exists before deletion
        Item savedItem = itemJpaRepository.getItem(item.getId());
        assertThat(savedItem).isNotNull();
        
        // Act
        itemJpaRepository.deleteItem(item.getId());
        
        // Assert
        assertThatThrownBy(() -> itemJpaRepository.getItem(item.getId()))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found");
    }
    
    @Test
    @DisplayName("When getting a non-existent item, then ItemNotFoundException is thrown")
    public void whenGettingNonExistentItem_thenItemNotFoundExceptionIsThrown() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        
        // Act & Assert
        assertThatThrownBy(() -> itemJpaRepository.getItem(nonExistentId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found with id: " + nonExistentId);
    }
    
    @Nested
    @DisplayName("Item validation tests")
    class ItemValidationTests {
        
        @ParameterizedTest
        @DisplayName("When saving items with different prices, then they are stored correctly")
        @CsvSource({
            "Cheap Item,Basic Description,1.99,http://test.com/cheap.jpg",
            "Standard Item,Good stuff,99.99,http://test.com/standard.jpg",
            "Premium Item,The best quality,999.99,http://test.com/premium.jpg"
        })
        void itemsWithDifferentPricesAreSavedCorrectly(String name, String description, 
                                                       double price, String imageUrl) {
            // Arrange
            Item item = new Item(name, description, price, imageUrl);
            
            // Act
            UUID id = itemJpaRepository.saveItem(item);
            Item retrievedItem = itemJpaRepository.getItem(id);
            
            // Assert
            assertThat(retrievedItem.getName()).isEqualTo(name);
            assertThat(retrievedItem.getPrice()).isEqualTo(price);
        }

        @Test
        @DisplayName("When creating item with null name, then validation exception is thrown")
        void whenCreatingItemWithNullName_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Item(null, "Valid Description", 100.0, "http://test.com/image.jpg"))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item name cannot be null or empty");
        }

        @Test
        @DisplayName("When creating item with empty name, then validation exception is thrown")
        void whenCreatingItemWithEmptyName_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Item("", "Valid Description", 100.0, "http://test.com/image.jpg"))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item name cannot be null or empty");

            assertThatThrownBy(() -> new Item("   ", "Valid Description", 100.0, "http://test.com/image.jpg"))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item name cannot be null or empty");
        }

        @Test
        @DisplayName("When creating item with null description, then validation exception is thrown")
        void whenCreatingItemWithNullDescription_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Item("Valid Name", null, 100.0, "http://test.com/image.jpg"))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item description cannot be null or empty");
        }

        @Test
        @DisplayName("When creating item with empty description, then validation exception is thrown")
        void whenCreatingItemWithEmptyDescription_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Item("Valid Name", "", 100.0, "http://test.com/image.jpg"))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item description cannot be null or empty");

            assertThatThrownBy(() -> new Item("Valid Name", "   ", 100.0, "http://test.com/image.jpg"))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item description cannot be null or empty");
        }

        @ParameterizedTest
        @DisplayName("When creating item with invalid price, then validation exception is thrown")
        @ValueSource(doubles = {0.0, -1.0, -100.0})
        void whenCreatingItemWithInvalidPrice_thenValidationExceptionIsThrown(double invalidPrice) {
            assertThatThrownBy(() -> new Item("Valid Name", "Valid Description", invalidPrice, "http://test.com/image.jpg"))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item price must be greater than 0");
        }

        @Test
        @DisplayName("When creating item with null image URL, then validation exception is thrown")
        void whenCreatingItemWithNullImageUrl_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Item("Valid Name", "Valid Description", 100.0, null))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item image URL cannot be null or empty");
        }

        @Test
        @DisplayName("When creating item with empty image URL, then validation exception is thrown")
        void whenCreatingItemWithEmptyImageUrl_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Item("Valid Name", "Valid Description", 100.0, ""))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item image URL cannot be null or empty");

            assertThatThrownBy(() -> new Item("Valid Name", "Valid Description", 100.0, "   "))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item image URL cannot be null or empty");
        }

        @Test
        @DisplayName("When multiple validation rules are violated, exception contains all violations")
        void whenMultipleValidationRulesViolated_exceptionContainsAllViolations() {
            assertThatThrownBy(() -> new Item("", "", -1.0, ""))
                .isInstanceOf(ItemValidationException.class)
                .hasMessageContaining("Item name cannot be null or empty")
                .hasMessageContaining("Item description cannot be null or empty")
                .hasMessageContaining("Item price must be greater than 0")
                .hasMessageContaining("Item image URL cannot be null or empty");
        }

        @Test
        @DisplayName("When creating item with valid data, no exception is thrown")
        void whenCreatingItemWithValidData_noExceptionIsThrown() {
            Item item = new Item("Valid Name", "Valid Description", 100.0, "http://test.com/image.jpg");

            assertThat(item.getName()).isEqualTo("Valid Name");
            assertThat(item.getDescription()).isEqualTo("Valid Description");
            assertThat(item.getPrice()).isEqualTo(100.0);
            assertThat(item.getImageUrl()).isEqualTo("http://test.com/image.jpg");
            assertThat(item.getId()).isNotNull();
            assertThat(item.getCreatedAt()).isNotNull();
            assertThat(item.getUpdatedAt()).isNotNull();
        }
    }
}
