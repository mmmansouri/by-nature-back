package com.bynature.application.service;

import com.bynature.domain.model.User;
import com.bynature.domain.repository.UserRepository;
import com.bynature.domain.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserSpringService implements UserService {

    private final UserRepository userRepository;

    public UserSpringService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public UUID createUser(User user) {
        return userRepository.saveUser(user);
    }

    @Override
    public UUID updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public User getUser(UUID userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public void updateUserActiveStatus(UUID userId, boolean active) {
        userRepository.updateUserActiveStatus(userId, active);
    }

    @Override
    public void updateUserLastLogin(UUID userId) {
        userRepository.updateUserLastLogin(userId);
    }

    @Override
    public Optional<User> getUserByCustomerId(UUID customerId) {
        return userRepository.getUserByCustomerId(customerId);
    }
}