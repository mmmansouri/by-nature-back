package com.bynature.adapters.out.persistence.jpa;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.out.persistence.jpa.adapter.CustomerRepositoryAdapter;
import com.bynature.domain.exception.CustomerNotFoundException;
import com.bynature.domain.exception.CustomerValidationException;
import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {CustomerRepositoryAdapter.class})
@DisplayName("Customer JPA Adapter Tests")
public class CustomerJpaAdapterTest extends AbstractByNatureTest {

    @Autowired
    private CustomerRepositoryAdapter customerRepositoryAdapter;

    @Test
    @DisplayName("When saving a customer, then it can be retrieved")
    public void whenSavingCustomer_thenItCanBeRetrieved() {
        // Arrange
        Customer customer = new Customer("John", "Doe", new Email("john.doe@example.com"),
                new PhoneNumber("+33612345678"));

        // Act
        UUID id = customerRepositoryAdapter.saveCustomer(customer);
        Customer retrievedCustomer = customerRepositoryAdapter.getCustomer(id);

        // Assert
        assertThat(retrievedCustomer).isNotNull();
        assertThat(retrievedCustomer.getId()).isEqualTo(id);
        assertThat(retrievedCustomer.getFirstName()).isEqualTo("John");
        assertThat(retrievedCustomer.getLastName()).isEqualTo("Doe");
        assertThat(retrievedCustomer.getEmail().email()).isEqualTo("john.doe@example.com");
        assertThat(retrievedCustomer.getPhoneNumber().number()).isEqualTo("+33612345678");
    }

    @Test
    @DisplayName("When updating a customer, then changes are persisted")
    public void whenUpdatingCustomer_thenChangesArePersisted() {
        // Arrange
        Customer customer = new Customer("Jane", "Smith", new Email("jane.smith@example.com"),
                new PhoneNumber("+33623456789"));
        customerRepositoryAdapter.saveCustomer(customer);

        // Create a new customer with the same ID but updated fields
        Customer updatedCustomer = new Customer(
                customer.getId(),
                "Jane",
                "Doe",
                new Email("jane.doe@example.com"),
                new PhoneNumber("+33623456789")
        );
        updatedCustomer.setCity("Paris");
        updatedCustomer.setStreet("Rue de la Paix");
        updatedCustomer.setStreetNumber("42");
        updatedCustomer.setRegion("Île-de-France");
        updatedCustomer.setPostalCode("75001");
        updatedCustomer.setCountry("France");

        // Act
        customerRepositoryAdapter.updateCustomer(updatedCustomer);
        Customer retrievedCustomer = customerRepositoryAdapter.getCustomer(customer.getId());

        // Assert
        assertThat(retrievedCustomer.getLastName()).isEqualTo("Doe");
        assertThat(retrievedCustomer.getEmail().email()).isEqualTo("jane.doe@example.com");
        assertThat(retrievedCustomer.getCity()).isEqualTo("Paris");
        assertThat(retrievedCustomer.getStreet()).isEqualTo("Rue de la Paix");
        assertThat(retrievedCustomer.getCountry()).isEqualTo("France");
    }

    @Test
    @DisplayName("When getting a non-existent customer, then CustomerNotFoundException is thrown")
    public void whenGettingNonExistentCustomer_thenCustomerNotFoundExceptionIsThrown() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> customerRepositoryAdapter.getCustomer(nonExistentId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("When deleting a customer, then it can no longer be retrieved")
    public void whenDeletingCustomer_thenItCanNoLongerBeRetrieved() {
        // Arrange
        Customer customer = new Customer("Robert", "Johnson", new Email("robert@example.com"),
                new PhoneNumber("+33634567890"));
        UUID id = customerRepositoryAdapter.saveCustomer(customer);

        // Verify customer exists before deletion
        Customer savedCustomer = customerRepositoryAdapter.getCustomer(id);
        assertThat(savedCustomer).isNotNull();

        // Act
        customerRepositoryAdapter.deleteCustomer(id);

        // Assert
        assertThatThrownBy(() -> customerRepositoryAdapter.getCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    @DisplayName("When updating a non-existent customer, then CustomerNotFoundException is thrown")
    public void whenUpdatingNonExistentCustomer_thenCustomerNotFoundExceptionIsThrown() {
        // Arrange
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Nonexistent",
                "Customer",
                new Email("nonexistent@example.com"),
                new PhoneNumber("+33645678901")
        );

        // Act & Assert
        assertThatThrownBy(() -> customerRepositoryAdapter.updateCustomer(customer))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Nested
    @DisplayName("Customer with address tests")
    class CustomerWithAddressTests {

        @Test
        @DisplayName("When saving customer with address, then address fields are persisted")
        public void whenSavingCustomerWithAddress_thenAddressFieldsArePersisted() {
            // Arrange
            Customer customer = new Customer("Michael", "Brown", new Email("michael@example.com"),
                    new PhoneNumber("+33656789012"));
            customer.setStreetNumber("15");
            customer.setStreet("Avenue des Champs-Élysées");
            customer.setCity("Paris");
            customer.setRegion("Île-de-France");
            customer.setPostalCode("75008");
            customer.setCountry("France");

            // Act
            UUID id = customerRepositoryAdapter.saveCustomer(customer);
            Customer retrievedCustomer = customerRepositoryAdapter.getCustomer(id);

            // Assert
            assertThat(retrievedCustomer.getStreetNumber()).isEqualTo("15");
            assertThat(retrievedCustomer.getStreet()).isEqualTo("Avenue des Champs-Élysées");
            assertThat(retrievedCustomer.getCity()).isEqualTo("Paris");
            assertThat(retrievedCustomer.getPostalCode()).isEqualTo("75008");
        }

        @Test
        @DisplayName("When updating customer address, then address fields are updated")
        public void whenUpdatingCustomerAddress_thenAddressFieldsAreUpdated() {
            // Arrange
            Customer customer = new Customer("Sarah", "Davis", new Email("sarah@example.com"),
                    new PhoneNumber("+33667890123"));
            // Initial address
            customer.setStreetNumber("10");
            customer.setStreet("Rue de Rivoli");
            customer.setCity("Paris");

            UUID id = customerRepositoryAdapter.saveCustomer(customer);

            // Get the saved customer and update the address
            Customer savedCustomer = customerRepositoryAdapter.getCustomer(id);
            savedCustomer.setStreetNumber("20");
            savedCustomer.setStreet("Boulevard Haussmann");
            savedCustomer.setPostalCode("75009");

            // Act
            customerRepositoryAdapter.updateCustomer(savedCustomer);
            Customer updatedCustomer = customerRepositoryAdapter.getCustomer(id);

            // Assert
            assertThat(updatedCustomer.getStreetNumber()).isEqualTo("20");
            assertThat(updatedCustomer.getStreet()).isEqualTo("Boulevard Haussmann");
            assertThat(updatedCustomer.getPostalCode()).isEqualTo("75009");
        }
    }

    @Nested
    @DisplayName("Customer validation tests")
    class CustomerValidationTests {

        @Test
        @DisplayName("When creating customer with null firstName, then validation exception is thrown")
        public void whenCreatingCustomerWithNullFirstName_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Customer(null, "Valid", new Email("valid@example.com"),
                    new PhoneNumber("+33678901234")))
                    .isInstanceOf(CustomerValidationException.class)
                    .hasMessageContaining("Le prénom ne peut pas être vide");
        }

        @Test
        @DisplayName("When creating customer with empty lastName, then validation exception is thrown")
        public void whenCreatingCustomerWithEmptyLastName_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Customer("Valid", "", new Email("valid@example.com"),
                    new PhoneNumber("+33678901234")))
                    .isInstanceOf(CustomerValidationException.class)
                    .hasMessageContaining("Le nom ne peut pas être vide");
        }

        @Test
        @DisplayName("When creating customer with null email, then validation exception is thrown")
        public void whenCreatingCustomerWithNullEmail_thenValidationExceptionIsThrown() {
            assertThatThrownBy(() -> new Customer("Valid", "Name", null,
                    new PhoneNumber("+33678901234")))
                    .isInstanceOf(CustomerValidationException.class)
                    .hasMessageContaining("L'email ne peut pas être null");
        }

        @Test
        @DisplayName("When setting empty street number, then validation exception is thrown")
        public void whenSettingEmptyStreetNumber_thenValidationExceptionIsThrown() {
            Customer customer = new Customer("Valid", "Name", new Email("valid@example.com"),
                    new PhoneNumber("+33678901234"));

            assertThatThrownBy(() -> customer.setStreetNumber(""))
                    .isInstanceOf(CustomerValidationException.class)
                    .hasMessageContaining("Le numéro de rue ne peut pas être vide");
        }

        @Test
        @DisplayName("When creating customer with valid data, no exception is thrown")
        public void whenCreatingCustomerWithValidData_noExceptionIsThrown() {
            // Arrange & Act
            Customer customer = new Customer("Valid", "Customer", new Email("valid@example.com"),
                    new PhoneNumber("+33678901234"));

            // Assert
            assertThat(customer.getFirstName()).isEqualTo("Valid");
            assertThat(customer.getLastName()).isEqualTo("Customer");
            assertThat(customer.getEmail().email()).isEqualTo("valid@example.com");
            assertThat(customer.getPhoneNumber().number()).isEqualTo("+33678901234");
            assertThat(customer.getId()).isNotNull();
        }
    }
}