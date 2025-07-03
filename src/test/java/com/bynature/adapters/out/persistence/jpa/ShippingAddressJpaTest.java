package com.bynature.adapters.out.persistence.jpa;

import com.bynature.adapters.out.persistence.jpa.adapter.CustomerRepositoryAdapter;
import com.bynature.adapters.out.persistence.jpa.adapter.ShippingAddressRepositoryAdapter;
import com.bynature.domain.exception.ShippingAddressNotFoundException;
import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.ShippingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ContextConfiguration(classes = {ShippingAddressRepositoryAdapter.class, CustomerRepositoryAdapter.class})
public class ShippingAddressJpaTest extends AbstractJpaTest {

    @Autowired
    private ShippingAddressRepositoryAdapter shippingAddressRepositoryAdapter;

    @Autowired
    private CustomerRepositoryAdapter customerRepositoryAdapter;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        // Get test customer - assuming it exists in test data
        customer = customerRepositoryAdapter.getCustomer(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"));
    }

    @Test
    public void whenSavingShippingAddress_thenItCanBeRetrieved() {
        // Create a shipping address
        ShippingAddress shippingAddress = new ShippingAddress(
                customer,
                "My address",
                "Jane",
                "Smith",
                new PhoneNumber("+33612345678"),
                new Email("jane.smith@example.com"),
                "42",
                "Rue des Fleurs",
                "Nice",
                "Provence-Alpes-Côte d'Azur",
                "06000",
                "France"
        );

        // Save the shipping address
        shippingAddressRepositoryAdapter.saveShippingAddress(shippingAddress);

        // Retrieve the shipping address by its id
        ShippingAddress retrievedAddress = shippingAddressRepositoryAdapter.getShippingAddress(shippingAddress.getId());

        // Assert that the retrieved address matches the saved one
        assertThat(retrievedAddress).isNotNull();
        assertThat(retrievedAddress.getId()).isEqualTo(shippingAddress.getId());
        assertThat(retrievedAddress.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(retrievedAddress.getFirstName()).isEqualTo("Jane");
        assertThat(retrievedAddress.getLastName()).isEqualTo("Smith");
        assertThat(retrievedAddress.getPhoneNumber().number()).isEqualTo("+33612345678");
        assertThat(retrievedAddress.getEmail().email()).isEqualTo("jane.smith@example.com");
        assertThat(retrievedAddress.getStreetNumber()).isEqualTo("42");
        assertThat(retrievedAddress.getStreet()).isEqualTo("Rue des Fleurs");
        assertThat(retrievedAddress.getCity()).isEqualTo("Nice");
        assertThat(retrievedAddress.getRegion()).isEqualTo("Provence-Alpes-Côte d'Azur");
        assertThat(retrievedAddress.getPostalCode()).isEqualTo("06000");
        assertThat(retrievedAddress.getCountry()).isEqualTo("France");
    }

    @Test
    public void whenUpdatingShippingAddress_thenChangesAreSaved() {
        // Create and save a shipping address
        ShippingAddress shippingAddress = new ShippingAddress(
                customer,
                "My address",
                "Robert",
                "Johnson",
                new PhoneNumber("+33698765432"),
                new Email("robert.johnson@example.com"),
                "15",
                "Avenue des Mimosas",
                "Cannes",
                "Provence-Alpes-Côte d'Azur",
                "06400",
                "France"
        );

        // Save and get the ID
        UUID savedId = shippingAddressRepositoryAdapter.saveShippingAddress(shippingAddress);

        // Retrieve the saved address before updating
        ShippingAddress retrievedAddress = shippingAddressRepositoryAdapter.getShippingAddress(savedId);

        // Update the retrieved address
        retrievedAddress.setStreetNumber("16");
        retrievedAddress.setStreet("Avenue des Roses");
        retrievedAddress.setCity("Antibes");

        shippingAddressRepositoryAdapter.updateShippingAddress(retrievedAddress);

        // Retrieve the updated shipping address
        ShippingAddress updatedAddress = shippingAddressRepositoryAdapter.getShippingAddress(savedId);

        // Assert that the changes were saved
        assertThat(updatedAddress.getStreetNumber()).isEqualTo("16");
        assertThat(updatedAddress.getStreet()).isEqualTo("Avenue des Roses");
        assertThat(updatedAddress.getCity()).isEqualTo("Antibes");
    }

    @Test
    public void whenFindingShippingAddressesByCustomerId_thenAddressesAreReturned() {
        // Get shipping addresses for the customer (should be from initial data)
        List<ShippingAddress> customerAddresses = shippingAddressRepositoryAdapter.getShippingAddressesByCustomer(customer.getId());

        // Assert that addresses are found
        assertThat(customerAddresses).isNotEmpty();
        assertThat(customerAddresses.get(0).getCustomer().getId()).isEqualTo(customer.getId());
    }

    @Test
    public void whenFindingShippingAddressesByCustomerIdAsync_thenAddressesAreReturned() throws ExecutionException, InterruptedException, TimeoutException {
        // Find shipping addresses asynchronously
        CompletableFuture<List<ShippingAddress>> futureAddresses =
                shippingAddressRepositoryAdapter.getShippingAddressesByCustomerIdAsync(customer.getId());

        // Get the result with a timeout
        List<ShippingAddress> customerAddresses = futureAddresses.get(5, TimeUnit.SECONDS);

        // Assert that addresses are found
        assertThat(customerAddresses).isNotEmpty();
        assertThat(customerAddresses.get(0).getCustomer().getId()).isEqualTo(customer.getId());
    }

    @Test
    public void whenFindingShippingAddressesByCustomerIdPaginated_thenPagedResultIsReturned() {
        // Find first page with limited number of addresses
        Page<ShippingAddress> addressPage = shippingAddressRepositoryAdapter.getShippingAddressesByCustomerIdPaginated(
                customer.getId(), PageRequest.of(0, 2));

        // Assert pagination works correctly
        assertThat(addressPage.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(addressPage.getContent()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    public void whenShippingAddressDoesNotExist_thenGetShippingAddressThrowsException() {
        // Try to retrieve a non-existent shipping address
        ShippingAddressNotFoundException exception = assertThrows(ShippingAddressNotFoundException.class,
                () -> shippingAddressRepositoryAdapter.getShippingAddress(UUID.randomUUID()));

        assertThat(exception.getShippingAddressId()).isNotNull();
    }

    @Test
    public void whenDeletingShippingAddress_thenItCannotBeRetrieved() {
        // Create and save a shipping address
        ShippingAddress shippingAddress = new ShippingAddress(
                customer,
                "To Delete",
                "Deleted",
                "User",
                new PhoneNumber("+33611223344"),
                new Email("deleted.user@example.com"),
                "99",
                "Rue to Delete",
                "Paris",
                "Île-de-France",
                "75001",
                "France"
        );

        shippingAddressRepositoryAdapter.saveShippingAddress(shippingAddress);

        // Delete the shipping address
        shippingAddressRepositoryAdapter.deleteShippingAddress(shippingAddress.getId());

        // Try to retrieve the deleted address
        assertThrows(ShippingAddressNotFoundException.class,
                () -> shippingAddressRepositoryAdapter.getShippingAddress(shippingAddress.getId()));
    }

    @Test
    public void whenNoShippingAddressesForCustomer_thenEmptyListIsReturned() {
        // Create a random UUID that doesn't exist in the database
        UUID randomCustomerId = UUID.randomUUID();

        // Try to find shipping addresses for this customer
        List<ShippingAddress> addresses = shippingAddressRepositoryAdapter.getShippingAddressesByCustomer(randomCustomerId);

        // Assert the list is empty
        assertThat(addresses).isEmpty();
    }

    @Test
    public void whenSavingShippingAddressWithLabel_thenLabelIsPersisted() {
        // Create a shipping address with a specific label
        String testLabel = "Primary Home";
        ShippingAddress shippingAddress = new ShippingAddress(
                customer,
                testLabel,
                "Jane",
                "Smith",
                new PhoneNumber("+33612345678"),
                new Email("jane.smith@example.com"),
                "42",
                "Rue des Fleurs",
                "Nice",
                "Provence-Alpes-Côte d'Azur",
                "06000",
                "France"
        );

        // Save the shipping address
        UUID savedId = shippingAddressRepositoryAdapter.saveShippingAddress(shippingAddress);

        // Retrieve the shipping address by its id
        ShippingAddress retrievedAddress = shippingAddressRepositoryAdapter.getShippingAddress(savedId);

        // Assert that the label is correctly persisted
        assertThat(retrievedAddress.getLabel()).isEqualTo(testLabel);
    }

    @Test
    public void whenUpdatingShippingAddressLabel_thenLabelIsUpdated() {
        // Create and save a shipping address
        ShippingAddress shippingAddress = new ShippingAddress(
                customer,
                "Original Label",
                "Robert",
                "Johnson",
                new PhoneNumber("+33698765432"),
                new Email("robert.johnson@example.com"),
                "15",
                "Avenue des Mimosas",
                "Cannes",
                "Provence-Alpes-Côte d'Azur",
                "06400",
                "France"
        );

        // Save and get the ID
        UUID savedId = shippingAddressRepositoryAdapter.saveShippingAddress(shippingAddress);

        // Retrieve the saved address
        ShippingAddress retrievedAddress = shippingAddressRepositoryAdapter.getShippingAddress(savedId);

        // Update the label
        String updatedLabel = "New Updated Label";
        retrievedAddress.setLabel(updatedLabel);
        shippingAddressRepositoryAdapter.updateShippingAddress(retrievedAddress);

        // Retrieve the updated shipping address
        ShippingAddress updatedAddress = shippingAddressRepositoryAdapter.getShippingAddress(savedId);

        // Assert that the label was updated
        assertThat(updatedAddress.getLabel()).isEqualTo(updatedLabel);
    }
}