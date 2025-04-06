package com.bynature.adapters.in.web.item;

import com.bynature.domain.model.Item;
import com.bynature.domain.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<UUID> createItem(@RequestBody ItemCreationRequest itemCreationRequest) {


        UUID createdItemUUID = itemService.createItem(itemCreationRequest.toDomain());

        // Return a 201 Created response with the location of the new Item.
        return ResponseEntity
                .created(URI.create("/items/" + createdItemUUID))
                .body(createdItemUUID);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRetrievalResponse> getItem(@PathVariable("id") UUID uuid) {

        Item item = itemService.getItem(uuid);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .body(ItemRetrievalResponse.fromDomain(item));
    }

    @GetMapping
    public ResponseEntity<List<ItemRetrievalResponse>> getAllItems() {

        List<Item> items = itemService.getAllItems();

        return ResponseEntity.ok(items.stream()
                .map(ItemRetrievalResponse::fromDomain)
                .collect(Collectors.toList()));
    }
}