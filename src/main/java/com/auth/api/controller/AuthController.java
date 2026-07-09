package com.auth.api.controller;

import com.auth.api.dto.*;
import com.auth.api.security.UserPrincipal;
import com.auth.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Registers a new user. Returns clientId, clientSecret (ONCE), applicationCode,
     * createDate, expiryDate, and JWT tokens.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse resp = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "User registered successfully. " +
                        "Save your clientSecret — it will NOT be shown again.", resp));
    }

    /**
     * POST /api/auth/login
     * Authenticates via clientId + clientSecret + applicationCode.
     * Returns JWT tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse resp = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", resp));
    }

    /**
     * GET /api/auth/me
     * Returns authenticated user info (requires Bearer token).
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me(
            @AuthenticationPrincipal UserPrincipal principal) {

        Map<String, Object> info = Map.of(
                "userId",          principal.getId(),
                "username",        principal.getUsername(),
                "email",           principal.getEmail(),
                "clientId",        principal.getClientId(),
                "applicationCode", principal.getApplicationCode(),
                "roles",           principal.getAuthorities()
        );
        return ResponseEntity.ok(ApiResponse.success("User info retrieved", info));
    }

    /** GET /api/auth/health */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth API is running", "OK"));
    }
}
