package com.bynature.domain.repository;

import com.bynature.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    UUID saveUser(User user);

    
    UUID updateUser(User user);

    User getUser(UUID userId);

    Optional<User> getUserByEmail(String email);

    void deleteUser(UUID userId);

    
    void updateUserActiveStatus(UUID userId, boolean active);

    
    void updateUserLastLogin(UUID userId);

    Optional<User> getUserByCustomerId(UUID customerId);
}
