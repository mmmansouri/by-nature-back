package com.bynature.config.security;

import com.bynature.domain.model.User;
import com.bynature.domain.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<User> userOptional = userRepository.getUserByEmail(email);

        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new BadCredentialsException("User account is not active");
        }

        // Create authorities from role and add custom scopes based on role
        var authorities = Stream.concat(
                Stream.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                getRoleScopes(user).stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
        ).collect(Collectors.toList());

        // Update last login time
        userRepository.updateUserLastLogin(user.getId());

        return new UsernamePasswordAuthenticationToken(
                user.getEmail().email(),
                user.getPassword(),
                authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private List<String> getRoleScopes(User user) {
        // Map roles to appropriate scopes
        switch (user.getRole()) {
            case ADMIN:
                return List.of("admin:read", "admin:write", "customer:read", "customer:write", "item:read", "item:write");
            case CUSTOMER:
                return List.of("customer:read", "customer:write", "item:read");
            default:
                return Collections.emptyList();
        }
    }
}