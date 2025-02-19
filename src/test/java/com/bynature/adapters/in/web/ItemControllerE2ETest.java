package com.bynature.adapters.in.web;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.in.web.dto.request.ItemCreationRequest;
import com.bynature.adapters.in.web.dto.response.ItemRetrievalResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerE2ETest extends AbstractByNatureTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCreateItem_shouldRetrieveIt_E2E() {
        // Création de la requête
        ItemCreationRequest request = new ItemCreationRequest(
                "Test Item",
                "Test Description",
                99.99,
                "test-image.jpg"
        );

        // Création de l'article
        ResponseEntity<UUID> createResponse = restTemplate.postForEntity(
                "/items",
                request,
                UUID.class
        );

        // Vérification de la création
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        UUID createdItemId = createResponse.getBody();

        // Récupération de l'article créé
        ResponseEntity<ItemRetrievalResponse> getResponse = restTemplate.getForEntity(
                "/items/" + createdItemId,
                ItemRetrievalResponse.class
        );

        // Vérification des données
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().id()).isEqualTo(createdItemId);
        assertThat(getResponse.getBody().name()).isEqualTo(request.name());
        assertThat(getResponse.getBody().description()).isEqualTo(request.description());
        assertThat(getResponse.getBody().price()).isEqualTo(request.price());
        assertThat(getResponse.getBody().imageUrl()).isEqualTo(request.imageUrl());
    }

    @Test
    public void whenGetAllItems_shouldReturnListOfItems_E2E() {
        // Récupération de tous les articles
        ResponseEntity<List<ItemRetrievalResponse>> response = restTemplate.exchange(
                "/items",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Vérifications
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    public void whenGetNonExistingItem_shouldReturn404_E2E() {
        // Tentative de récupération d'un article inexistant
        ResponseEntity<ItemRetrievalResponse> response = restTemplate.getForEntity(
                "/items/" + UUID.randomUUID(),
                ItemRetrievalResponse.class
        );

        // Vérification
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
