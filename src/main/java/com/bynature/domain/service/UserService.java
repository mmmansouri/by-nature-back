package com.bynature.domain.service;

import com.bynature.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UUID createUser(User user);

    UUID updateUser(User user);

    void deleteUser(UUID userId);

    User getUser(UUID userId);

    Optional<User> getUserByEmail(String email);

    void updateUserActiveStatus(UUID userId, boolean active);

    void updateUserLastLogin(UUID userId);

    Optional<User> getUserByCustomerId(UUID customerId);
}
