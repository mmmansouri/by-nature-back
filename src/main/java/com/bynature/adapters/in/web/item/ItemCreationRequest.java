package com.bynature.adapters.in.web.item;

import com.bynature.domain.model.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

public record ItemCreationRequest( @NotBlank(message = "Item name is required")
                                   String name,

                                   @NotBlank(message = "Item description is required")
                                   String description,

                                   @Positive(message = "Price must be greater than zero")
                                   double price,

                                   @URL(message = "Image URL must be a valid URL")
                                   String imageUrl) {

    public Item toDomain() {
        return new Item(name, description, price, imageUrl);
    }
}
