package com.bynature.adapters.in.web.user;

import com.bynature.adapters.in.web.user.dto.UserCreationRequest;
import com.bynature.adapters.in.web.user.dto.UserEmailUpdateRequest;
import com.bynature.adapters.in.web.user.dto.UserPasswordUpdateRequest;
import com.bynature.adapters.in.web.user.dto.UserRetrievalResponse;
import com.bynature.domain.exception.UserNotFoundException;
import com.bynature.domain.model.User;
import com.bynature.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UUID> createUser(@Valid @RequestBody UserCreationRequest userCreationRequest) {
        UUID createdUserId = userService.createUser(userCreationRequest.toDomain());

        return ResponseEntity
                .created(URI.create("/users/" + createdUserId))
                .body(createdUserId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRetrievalResponse> getUser(@PathVariable("id") UUID uuid) {
        try {
            User user = userService.getUser(uuid);
            return ResponseEntity.ok(UserRetrievalResponse.fromDomain(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable("id") UUID uuid) {
        userService.updateUserActiveStatus(uuid, true);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable("id") UUID uuid) {
        userService.updateUserActiveStatus(uuid, false);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<Void> updateUserEmail(
            @PathVariable("id") UUID uuid,
            @Valid @RequestBody UserEmailUpdateRequest updateRequest) {
        User user = userService.getUser(uuid);
        user.setEmail(updateRequest.toEmail());
        userService.updateUser(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable("id") UUID uuid,
            @Valid @RequestBody UserPasswordUpdateRequest passwordRequest) {
        // TODO : In a real application, you would verify the current password here
        User user = userService.getUser(uuid);
        user.setPassword(passwordRequest.newPassword());
        userService.updateUser(user);
        return ResponseEntity.noContent().build();
    }
}
