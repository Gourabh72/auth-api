package com.auth.api.service;

import com.auth.api.dto.AuthResponse;
import com.auth.api.dto.LoginRequest;
import com.auth.api.dto.RegisterRequest;
import com.auth.api.entity.Role;
import com.auth.api.entity.User;
import com.auth.api.repository.UserRepository;
import com.auth.api.security.JwtUtils;
import com.auth.api.security.UserDetailsServiceImpl;
import com.auth.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository          userRepository;
    private final PasswordEncoder         passwordEncoder;
    private final JwtUtils                jwtUtils;

    // ── Register ──────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest req) {

        // ── Uniqueness checks ───────────────────────────────────────────────
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username '" + req.getUsername() + "' is already taken");

        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email '" + req.getEmail() + "' is already registered");

        if (userRepository.existsByApplicationCode(req.getApplicationCode()))
            throw new IllegalArgumentException("Application code '" + req.getApplicationCode() + "' is already registered");

        // ── Generate client credentials ─────────────────────────────────────
        String rawClientId     = UUID.randomUUID().toString();          // plain
        String rawClientSecret = UUID.randomUUID().toString()           // plain — returned ONCE
                                 + "-" + UUID.randomUUID().toString();

        // ── Expiry (default: 1 year) ────────────────────────────────────────
        LocalDateTime expiry = (req.getExpiryDate() != null)
                ? req.getExpiryDate()
                : LocalDateTime.now().plusYears(1);

        // ── Persist user ────────────────────────────────────────────────────
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .clientId(rawClientId)
                .clientSecret(passwordEncoder.encode(rawClientSecret))  // hashed
                .applicationCode(req.getApplicationCode())
                .createDate(LocalDateTime.now())
                .expiryDate(expiry)
                .roles(resolveRoles(req.getRoles()))
                .build();

        user = userRepository.save(user);
        log.info("Registered user '{}' | appCode='{}' | clientId='{}'",
                user.getUsername(), user.getApplicationCode(), rawClientId);

        // ── Issue tokens ────────────────────────────────────────────────────
        UserPrincipal principal = UserPrincipal.build(user);
        String accessToken  = jwtUtils.generateAccessToken(principal);

        // clientSecret returned in plain text ONLY HERE — never again
        return buildResponse( accessToken);
    }

    // ── Login (clientId + clientSecret + applicationCode) ─────────────────────

    public AuthResponse login(LoginRequest req) {

        // 1. Look up by clientId
        User user = userRepository.findByClientId(req.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid client credentials"));

        // 2. Verify applicationCode matches
        if (!user.getApplicationCode().equals(req.getApplicationCode()))
            throw new IllegalArgumentException("Invalid client credentials");

        // 3. Verify clientSecret
        if (!passwordEncoder.matches(req.getClientSecret(), user.getClientSecret()))
            throw new IllegalArgumentException("Invalid client credentials");

        // 4. Check account enabled
        if (!user.isEnabled())
            throw new IllegalStateException("Account is disabled");

        // 5. Check expiry
        if (user.isExpired())
            throw new IllegalStateException(
                "Client credentials expired on " + user.getExpiryDate());

        UserPrincipal principal = UserPrincipal.build(user);
        String accessToken  = jwtUtils.generateAccessToken(principal);

        log.info("Login successful for clientId='{}'", req.getClientId());

        // clientSecret NOT returned on login
        return buildResponse(accessToken);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Set<Role> resolveRoles(Set<String> requested) {
        if (requested == null || requested.isEmpty())
            return new HashSet<>(Set.of(Role.ROLE_USER));

        return requested.stream().map(r -> switch (r.toLowerCase()) {
            case "admin"     -> Role.ROLE_ADMIN;
            case "moderator" -> Role.ROLE_MODERATOR;
            default          -> Role.ROLE_USER;
        }).collect(Collectors.toSet());
    }

    private AuthResponse buildResponse(
                                       String accessToken) {

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }
}
