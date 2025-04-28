package com.bynature.domain.model;

import com.bynature.domain.exception.ItemValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;

class ItemTest {

    private final String validName = "Test Item";
    private final String validDescription = "This is a test item";
    private final double validPrice = 29.99;
    private final String validImageUrl = "https://example.com/image.jpg";
    private final UUID validId = UUID.randomUUID();
    private final LocalDateTime validCreatedAt = LocalDateTime.now();
    private final LocalDateTime validUpdatedAt = LocalDateTime.now();

    @Nested
    @DisplayName("Item Creation Tests")
    class ItemCreationTests {
        @Test
        @DisplayName("Should create item with valid parameters")
        void shouldCreateItemWithValidParameters() {
            var item = new Item(validName, validDescription, validPrice, validImageUrl);

            assertThat(item.getId()).isNotNull();
            assertThat(item.getName()).isEqualTo(validName);
            assertThat(item.getDescription()).isEqualTo(validDescription);
            assertThat(item.getPrice()).isEqualTo(validPrice);
            assertThat(item.getImageUrl()).isEqualTo(validImageUrl);
            assertThat(item.getCreatedAt()).isCloseTo(LocalDateTime.now(), within(2, java.time.temporal.ChronoUnit.SECONDS));
            assertThat(item.getUpdatedAt()).isCloseTo(LocalDateTime.now(), within(2, java.time.temporal.ChronoUnit.SECONDS));
        }

        @Test
        @DisplayName("Should create item with all provided parameters")
        void shouldCreateItemWithAllProvidedParameters() {
            var item = new Item(validId, validName, validDescription, validPrice, validImageUrl,
                    validCreatedAt, validUpdatedAt);

            assertThat(item.getId()).isEqualTo(validId);
            assertThat(item.getName()).isEqualTo(validName);
            assertThat(item.getDescription()).isEqualTo(validDescription);
            assertThat(item.getPrice()).isEqualTo(validPrice);
            assertThat(item.getImageUrl()).isEqualTo(validImageUrl);
            assertThat(item.getCreatedAt()).isEqualTo(validCreatedAt);
            assertThat(item.getUpdatedAt()).isEqualTo(validUpdatedAt);
        }
    }

    @Nested
    @DisplayName("Item Validation Tests")
    class ItemValidationTests {
        @Test
        @DisplayName("Should throw exception when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThatExceptionOfType(ItemValidationException.class)
                    .isThrownBy(() -> new Item(null, validName, validDescription, validPrice, validImageUrl,
                            validCreatedAt, validUpdatedAt))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Item ID cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when createdAt is null")
        void shouldThrowExceptionWhenCreatedAtIsNull() {
            assertThatExceptionOfType(ItemValidationException.class)
                    .isThrownBy(() -> new Item(validId, validName, validDescription, validPrice, validImageUrl,
                            null, validUpdatedAt))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Item creation date cannot be null"));
        }

        @Test
        @DisplayName("Should throw exception when updatedAt is null")
        void shouldThrowExceptionWhenUpdatedAtIsNull() {
            assertThatExceptionOfType(ItemValidationException.class)
                    .isThrownBy(() -> new Item(validId, validName, validDescription, validPrice, validImageUrl,
                            validCreatedAt, null))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Item update date cannot be null"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when name is invalid")
        void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
            assertThatExceptionOfType(ItemValidationException.class)
                    .isThrownBy(() -> new Item(invalidName, validDescription, validPrice, validImageUrl))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Item name cannot be null or empty"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when description is invalid")
        void shouldThrowExceptionWhenDescriptionIsInvalid(String invalidDescription) {
            assertThatExceptionOfType(ItemValidationException.class)
                    .isThrownBy(() -> new Item(validName, invalidDescription, validPrice, validImageUrl))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Item description cannot be null or empty"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0, -1, -0.01, -10})
        @DisplayName("Should throw exception when price is not positive")
        void shouldThrowExceptionWhenPriceIsNotPositive(double invalidPrice) {
            assertThatExceptionOfType(ItemValidationException.class)
                    .isThrownBy(() -> new Item(validName, validDescription, invalidPrice, validImageUrl))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Item price must be greater than 0"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("Should throw exception when imageUrl is invalid")
        void shouldThrowExceptionWhenImageUrlIsInvalid(String invalidImageUrl) {
            assertThatExceptionOfType(ItemValidationException.class)
                    .isThrownBy(() -> new Item(validName, validDescription, validPrice, invalidImageUrl))
                    .satisfies(e -> assertThat(e.getViolations()).contains("Item image URL cannot be null or empty"));
        }
    }

    @Test
    @DisplayName("Should update the updatedAt timestamp")
    void shouldUpdateTheUpdatedAtTimestamp() {
        var item = new Item(validName, validDescription, validPrice, validImageUrl);
        var initialUpdatedAt = item.getUpdatedAt();

        // Wait a moment to ensure timestamp difference
        try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }

        var newTime = LocalDateTime.now();
        item.setUpdatedAt(newTime);

        assertThat(item.getUpdatedAt())
                .isNotEqualTo(initialUpdatedAt)
                .isEqualTo(newTime);
    }
}