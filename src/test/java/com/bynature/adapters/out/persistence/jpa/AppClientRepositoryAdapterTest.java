package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.adapter.AppClientRepositoryAdapter;
import com.bynature.domain.model.AppClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {AppClientRepositoryAdapter.class})
@DisplayName("AppClient Repository Adapter Tests")
public class AppClientRepositoryAdapterTest extends AbstractJpaTest {

    @Autowired
    private AppClientRepositoryAdapter appClientRepositoryAdapter;

    @Test
    @DisplayName("When finding app client by existing ID, then it is returned")
    public void whenFindingByExistingAppClientId_thenClientIsReturned() {
        // Act
        List<AppClient> clients = appClientRepositoryAdapter.findByAppClientId("bynature-front");

        // Assert
        assertThat(clients).isNotEmpty();
        assertThat(clients).hasSize(1);
        AppClient client = clients.getFirst();
        assertThat(client.getId()).isEqualTo(UUID.fromString("a2c68ee4-9c3f-4c9a-8d7b-f1a8e6357e42"));
        assertThat(client.getAppClientId()).isEqualTo("bynature-front");
        assertThat(client.isActive()).isTrue();
        assertThat(client.getAllowedOrigin()).isEqualTo("http://localhost:4200");
        // We don't check the client secret as it's hashed
    }

    @Test
    @DisplayName("When finding app client by non-existent ID, then empty list is returned")
    public void whenFindingByNonExistentAppClientId_thenEmptyListIsReturned() {
        // Act
        List<AppClient> clients = appClientRepositoryAdapter.findByAppClientId("non-existent-client");

        // Assert
        assertThat(clients).isEmpty();
    }
}
