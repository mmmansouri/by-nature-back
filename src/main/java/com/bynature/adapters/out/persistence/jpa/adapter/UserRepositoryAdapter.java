package com.bynature.adapters.out.persistence.jpa.adapter;

import com.bynature.adapters.out.persistence.jpa.adapter.mapper.EntityMapper;
import com.bynature.adapters.out.persistence.jpa.entity.CustomerEntity;
import com.bynature.adapters.out.persistence.jpa.entity.UserEntity;
import com.bynature.adapters.out.persistence.jpa.repository.CustomerJpaRepository;
import com.bynature.adapters.out.persistence.jpa.repository.UserJpaRepository;
import com.bynature.domain.exception.CustomerNotFoundException;
import com.bynature.domain.exception.UserNotFoundException;
import com.bynature.domain.model.User;
import com.bynature.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryAdapter.class);
    private final UserJpaRepository userJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, CustomerJpaRepository customerJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    @Transactional
    public UUID saveUser(User user) {
        log.debug("Saving user with email: {}", user.getEmail().email());

        UserEntity userEntity = UserEntity.fromDomain(user);

        // If user has a customer, fetch the actual entity from the database
        if (user.getCustomer() != null && user.getCustomer().getId() != null) {
            CustomerEntity customerEntity = customerJpaRepository
                    .findById(user.getCustomer().getId())
                    .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " +
                            user.getCustomer().getId()));
            userEntity.setCustomer(customerEntity);
        }

        log.info("User updated with ID: {}", user.getId());

        return userJpaRepository
                .save(userEntity)
                .getId();
    }

    @Transactional
    @Override
    public UUID updateUser(User user) {
        log.debug("Updating user with ID: {}", user.getId());

        // Verify user exists before updating
        getUser(user.getId());

        return this.saveUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(UUID userId) {
        log.debug("Fetching user with ID: {}", userId);

        return userJpaRepository.findById(userId)
                .map(EntityMapper::mapUserToDomain)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);

        return userJpaRepository.findByEmail(email)
                .map(EntityMapper::mapUserToDomain);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        log.debug("Deleting user with ID: {}", userId);

        // Verify user exists before deleting
        getUser(userId);

        userJpaRepository.deleteById(userId);

        log.info("User deleted with ID: {}", userId);
    }

    @Transactional
    @Override
    public void updateUserActiveStatus(UUID userId, boolean active) {
        log.debug("Updating active status to {} for user ID: {}", active, userId);

        // Verify user exists
        getUser(userId);

        userJpaRepository.updateUserActiveStatus(userId, active, LocalDateTime.now());

        log.info("User active status updated for ID: {}", userId);
    }

    @Transactional
    @Override
    public void updateUserLastLogin(UUID userId) {
        log.debug("Updating last login time for user ID: {}", userId);

        // Verify user exists
        getUser(userId);

        LocalDateTime now = LocalDateTime.now();
        userJpaRepository.updateUserLastLogin(userId, now, now);

        log.info("User last login updated for ID: {}", userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByCustomerId(UUID customerId) {
        log.debug("Fetching user with customer ID: {}", customerId);

        return userJpaRepository.findByCustomer_Id(customerId)
                .map(EntityMapper::mapUserToDomain) ;
    }

}