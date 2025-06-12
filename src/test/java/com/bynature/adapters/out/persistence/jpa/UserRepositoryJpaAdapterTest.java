package com.bynature.adapters.out.persistence.jpa;

import com.bynature.AbstractByNatureTest;
import com.bynature.adapters.out.persistence.jpa.adapter.CustomerRepositoryAdapter;
import com.bynature.adapters.out.persistence.jpa.adapter.UserRepositoryAdapter;
import com.bynature.domain.exception.UserNotFoundException;
import com.bynature.domain.model.Customer;
import com.bynature.domain.model.Email;
import com.bynature.domain.model.PhoneNumber;
import com.bynature.domain.model.Role;
import com.bynature.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {UserRepositoryAdapter.class, CustomerRepositoryAdapter.class})
@DisplayName("User JPA Adapter Tests")
public class UserRepositoryJpaAdapterTest extends AbstractByNatureTest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private CustomerRepositoryAdapter customerRepositoryAdapter;

    private Customer testCustomer;
    private User testUser;
    private final String TEST_PASSWORD = "hashedPassword123";

    @BeforeEach
    void setUp() {

        testCustomer = customerRepositoryAdapter
                .getCustomer(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"));
        testUser = userRepositoryAdapter
                .getUser(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"));
    }

    @Nested
    @DisplayName("Basic CRUD operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("When saving a user, then it can be retrieved")
        void whenSavingUser_thenItCanBeRetrieved() {
            // Arrange
            User user = new User(new Email("test@example.com"), TEST_PASSWORD, Role.ADMIN);

            // Act
            UUID userId = userRepositoryAdapter.saveUser(user);
            User retrievedUser = userRepositoryAdapter.getUser(userId);

            // Assert
            assertThat(retrievedUser).isNotNull();
            assertThat(retrievedUser.getId()).isEqualTo(userId);
            assertThat(retrievedUser.getEmail().email()).isEqualTo("test@example.com");
            assertThat(retrievedUser.getPassword()).isEqualTo(TEST_PASSWORD);
            assertThat(retrievedUser.getRole()).isEqualTo(Role.ADMIN);
            assertThat(retrievedUser.isActive()).isTrue();
        }

        @Test
        @DisplayName("When updating a user, then changes are persisted")
        void whenUpdatingUser_thenChangesArePersisted() {
            // Arrange
            User user = new User(new Email("original@example.com"), TEST_PASSWORD, Role.CUSTOMER);
            UUID userId = userRepositoryAdapter.saveUser(user);
            User savedUser = userRepositoryAdapter.getUser(userId);

            // Modify the user
            savedUser.setEmail(new Email("updated@example.com"));
            savedUser.setRole(Role.ADMIN);
            savedUser.setActive(false);

            // Act
            userRepositoryAdapter.saveUser(savedUser);
            User updatedUser = userRepositoryAdapter.getUser(userId);

            // Assert
            assertThat(updatedUser.getEmail().email()).isEqualTo("updated@example.com");
            assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);
            assertThat(updatedUser.isActive()).isFalse();
            assertThat(updatedUser.getUpdatedAt()).isAfterOrEqualTo(savedUser.getUpdatedAt());
        }

        @Test
        @DisplayName("When deleting a user, then it can no longer be retrieved")
        void whenDeletingUser_thenItCanNoLongerBeRetrieved() {
            // Arrange
            User user = new User(new Email("delete@example.com"), TEST_PASSWORD, Role.CUSTOMER);
            UUID userId = userRepositoryAdapter.saveUser(user);

            // Verify user exists before deletion
            User savedUser = userRepositoryAdapter.getUser(userId);
            assertThat(savedUser).isNotNull();

            // Act
            userRepositoryAdapter.deleteUser(userId);

            // Assert
            assertThatThrownBy(() -> userRepositoryAdapter.getUser(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + userId);
        }

        @Test
        @DisplayName("When getting a non-existent user, then UserNotFoundException is thrown")
        void whenGettingNonExistentUser_thenUserNotFoundExceptionIsThrown() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> userRepositoryAdapter.getUser(nonExistentId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + nonExistentId);
        }
    }

    @Nested
    @DisplayName("User-specific operations")
    class UserSpecificOperations {

        @Test
        @DisplayName("When updating user active status, then status is updated")
        void whenUpdatingUserActiveStatus_thenStatusIsUpdated() {
            // Arrange
            UUID userId = UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402");

            // Act - Deactivate
            userRepositoryAdapter.updateUserActiveStatus(userId, false);

            clearAndFlush();

            User deactivatedUser = userRepositoryAdapter.getUser(userId);

            // Assert
            assertThat(deactivatedUser.isActive()).isFalse();

            // Act - Reactivate
            userRepositoryAdapter.updateUserActiveStatus(userId, true);

            clearAndFlush();

            User reactivatedUser = userRepositoryAdapter.getUser(userId);

            // Assert
            assertThat(reactivatedUser.isActive()).isTrue();
        }

        @Test
        @DisplayName("When updating user last login, then last login date is updated")
        void whenUpdatingUserLastLogin_thenLastLoginDateIsUpdated() {
            // Arrange
            User user = new User(new Email("login@example.com"), TEST_PASSWORD, Role.CUSTOMER);
            UUID userId = userRepositoryAdapter.saveUser(user);
            LocalDateTime initialLastLogin = user.getLastLoginAt();

            // Wait to ensure time difference
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Act
            userRepositoryAdapter.updateUserLastLogin(userId);
            User updatedUser = userRepositoryAdapter.getUser(userId);

            // Assert
            assertThat(updatedUser.getLastLoginAt()).isAfterOrEqualTo(initialLastLogin);
            assertThat(updatedUser.getUpdatedAt()).isAfterOrEqualTo(user.getUpdatedAt());
        }

        @Test
        @DisplayName("When finding user by email, then correct user is returned")
        void whenFindingUserByEmail_thenCorrectUserIsReturned() {
            // Arrange
            String email = "findme@example.com";
            User user = new User(new Email(email), TEST_PASSWORD, Role.CUSTOMER);
            userRepositoryAdapter.saveUser(user);

            // Act
            Optional<User> foundUser = userRepositoryAdapter.getUserByEmail(email);

            // Assert
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getEmail().email()).isEqualTo(email);
        }

        @Test
        @DisplayName("When finding non-existent email, then empty Optional is returned")
        void whenFindingNonExistentEmail_thenEmptyOptionalIsReturned() {
            // Act
            Optional<User> foundUser = userRepositoryAdapter.getUserByEmail("nonexistent@example.com");

            // Assert
            assertThat(foundUser).isEmpty();
        }
    }

    @Nested
    @DisplayName("User-customer relationship tests")
    class UserCustomerRelationshipTests {

        @Test
        @DisplayName("When saving user with customer, then relationship is persisted")
        void whenSavingUserWithCustomer_thenRelationshipIsPersisted() {
            // Arrange
            User user = new User(new Email("customer-user@example.com"), TEST_PASSWORD, testCustomer);

            // Act
            UUID userId = userRepositoryAdapter.saveUser(user);
            User retrievedUser = userRepositoryAdapter.getUser(userId);

            // Assert
            assertThat(retrievedUser.getCustomer()).isNotNull();
            assertThat(retrievedUser.getCustomer().getId()).isEqualTo(testCustomer.getId());
            assertThat(retrievedUser.getCustomer().getEmail().email()).isEqualTo(testCustomer.getEmail().email());
        }

        @Test
        @DisplayName("When finding user by customer ID, then correct user is returned")
        void whenFindingUserByCustomerId_thenCorrectUserIsReturned() {

            // Act
            Optional<User> foundUser = userRepositoryAdapter.getUserByCustomerId(testCustomer.getId());

            // Assert
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getCustomer().getId()).isEqualTo(testCustomer.getId());
        }

        @Test
        @DisplayName("When finding non-existent customer ID, then empty Optional is returned")
        void whenFindingNonExistentCustomerId_thenEmptyOptionalIsReturned() {
            // Act
            Optional<User> foundUser = userRepositoryAdapter.getUserByCustomerId(UUID.randomUUID());

            // Assert
            assertThat(foundUser).isEmpty();
        }

        @Test
        @DisplayName("When linking user to customer, then relationship is updated")
        void whenLinkingUserToCustomer_thenRelationshipIsUpdated() {
            // Arrange
            User user = new User(new Email("link-test@example.com"), TEST_PASSWORD, Role.CUSTOMER);
            UUID userId = userRepositoryAdapter.saveUser(user);

            // Initial verification
            User initialUser = userRepositoryAdapter.getUser(userId);
            assertThat(initialUser.getCustomer()).isNull();

            // Act - Link to customer
            initialUser.linkToCustomer(testCustomer);
            userRepositoryAdapter.saveUser(initialUser);

            // Assert
            User updatedUser = userRepositoryAdapter.getUser(userId);
            assertThat(updatedUser.getCustomer()).isNotNull();
            assertThat(updatedUser.getCustomer().getId()).isEqualTo(testCustomer.getId());
        }
    }

    @Nested
    @DisplayName("Error handling tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("When updating non-existent user, then UserNotFoundException is thrown")
        void whenUpdatingNonExistentUser_thenUserNotFoundExceptionIsThrown() {
            // Arrange
            User nonExistentUser = new User(
                    UUID.randomUUID(),
                    new Email("nonexistent@example.com"),
                    TEST_PASSWORD,
                    null,
                    true,
                    Role.CUSTOMER,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            // Act & Assert
            assertThatThrownBy(() -> userRepositoryAdapter.updateUser(nonExistentUser))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("When updating active status for non-existent user, then UserNotFoundException is thrown")
        void whenUpdatingActiveStatusForNonExistentUser_thenUserNotFoundExceptionIsThrown() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> userRepositoryAdapter.updateUserActiveStatus(nonExistentId, true))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("When updating last login for non-existent user, then UserNotFoundException is thrown")
        void whenUpdatingLastLoginForNonExistentUser_thenUserNotFoundExceptionIsThrown() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act & Assert
            assertThatThrownBy(() -> userRepositoryAdapter.updateUserLastLogin(nonExistentId))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Test
    @DisplayName("When creating user without customer, then user is saved successfully")
    public void whenCreatingUserWithoutCustomer_thenUserIsSavedSuccessfully() {
        // Arrange
        User user = new User(
                new Email("no.customer@example.com"),
                "hashedPassword123",
                Role.VENDOR
        );

        // Act
        UUID id = userRepositoryAdapter.saveUser(user);
        User savedUser = userRepositoryAdapter.getUser(id);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getCustomer()).isNull();
        assertThat(savedUser.getEmail().email()).isEqualTo("no.customer@example.com");
        assertThat(savedUser.getRole()).isEqualTo(Role.VENDOR);
    }

    @Test
    @DisplayName("When linking multiple users to same customer, then constraint violation occurs")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void whenLinkingMultipleUsersToSameCustomer_thenConstraintViolationOccurs() {

        // First user - set the customer
        User firstUser = userRepositoryAdapter.getUser(UUID.fromString("b48ac10b-58cc-4372-a567-0e02b2c3d402"));
        firstUser.linkToCustomer(testCustomer);

        // Act & Assert - should throw exception when trying to link second user to same customer
        assertThatThrownBy(() -> userRepositoryAdapter.saveUser(firstUser))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("constraint");
    }

    @Test
    @DisplayName("When updating user to null customer, then update succeeds")
    public void whenUpdatingUserToNullCustomer_thenUpdateSucceeds() {
        // Arrange
        Customer customer = new Customer(testUser,
                "Alex", "Brown", new Email("alex.brown@example.com"),
                new PhoneNumber("+33612345999"),
                "45", "Rue Saint-Antoine", "Paris", "Île-de-France", "75004", "France"
        );
        UUID customerId = customerRepositoryAdapter.saveCustomer(customer);
        Customer savedCustomer = customerRepositoryAdapter.getCustomer(customerId);

        User user = new User(
                new Email("has.customer@example.com"),
                "hashedPassword123",
                savedCustomer
        );
        UUID userId = userRepositoryAdapter.saveUser(user);

        // Get saved user and remove customer link
        User savedUser = userRepositoryAdapter.getUser(userId);
        savedUser.linkToCustomer(null);

        // Act
        userRepositoryAdapter.saveUser(savedUser);
        User updatedUser = userRepositoryAdapter.getUser(userId);

        // Assert
        assertThat(updatedUser.getCustomer()).isNull();
    }

    @Test
    @DisplayName("When customer is freed from one user, another user can link to it")
    public void whenCustomerIsFreedFromOneUser_anotherUserCanLinkToIt() {

        // Remove customer from first user
        User retrievedFirstUser = userRepositoryAdapter.getUser(UUID.fromString("a47ac10b-58cc-4372-a567-0e02b2c3d401"));
        retrievedFirstUser.linkToCustomer(null);
        userRepositoryAdapter.saveUser(retrievedFirstUser);

        // Now link second user to the customer
        User retrievedSecondUser = userRepositoryAdapter.getUser(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"));
        retrievedSecondUser.linkToCustomer(testCustomer);

        // Act
        userRepositoryAdapter.saveUser(retrievedSecondUser);
        User updatedSecondUser = userRepositoryAdapter.getUser(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"));

        // Assert
        assertThat(updatedSecondUser.getCustomer()).isNotNull();
        assertThat(updatedSecondUser.getCustomer().getId()).isEqualTo(testCustomer.getId());
    }
}