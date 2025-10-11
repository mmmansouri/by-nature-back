package com.bynature.application.service;

import com.bynature.domain.model.User;
import com.bynature.domain.repository.UserRepository;
import com.bynature.domain.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserSpringService implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserSpringService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveUser(user);
    }

    @Override
    public User updateUser(User user) {
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