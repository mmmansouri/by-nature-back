package com.bynature.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OAuth2JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String SCOPE_CLAIM = "scope";
    private static final String ROLE_CLAIM = "roles";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add scopes as authorities with SCOPE_ prefix
        List<String> scopes = extractClaim(jwt, SCOPE_CLAIM);
        if (scopes != null) {
            authorities.addAll(scopes.stream()
                    .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                    .collect(Collectors.toList()));
        }

        // Add roles as authorities with ROLE_ prefix
        List<String> roles = extractClaim(jwt, ROLE_CLAIM);
        if (roles != null) {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList()));
        }

        return authorities;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractClaim(Jwt jwt, String claimName) {
        Object claim = jwt.getClaim(claimName);
        if (claim instanceof String) {
            return List.of(((String) claim).split(" "));
        }
        if (claim instanceof List) {
            return (List<String>) claim;
        }
        return List.of();
    }
}