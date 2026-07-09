package com.auth.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 100)
    private String fullName;

    // ── Client Credential Fields ───────────────────────────────────────────────

    /** Unique code identifying the application (e.g. "APP-BILLING-001") */
    @NotBlank(message = "Application code is required")
    @Size(min = 3, max = 50, message = "Application code must be 3–50 characters")
    private String applicationCode;

    /**
     * Expiry date for the client credentials.
     * Format: "2025-12-31T23:59:59"
     * Defaults to 1 year from now if not provided.
     */
    private LocalDateTime expiryDate;

    /** Optional roles; defaults to ROLE_USER */
    private Set<String> roles;
}
