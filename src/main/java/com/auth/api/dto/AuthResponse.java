package com.auth.api.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    /*private Long expiresIn;          // ms

    // ── User Info ──────────────────────────────────────────────────────────────
    private Long   userId;
    private String username;
    private String email;
    private Set<String> roles;

    // ── Client Credential Info ─────────────────────────────────────────────────
    private String clientId;
    private String clientSecret;     // plain-text, shown ONCE at registration
    private String applicationCode;
    private LocalDateTime createDate;
    private LocalDateTime expiryDate;*/
}
