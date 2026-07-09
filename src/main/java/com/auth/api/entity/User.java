package com.auth.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "client_id"),
           @UniqueConstraint(columnNames = "application_code")
       })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    // ── Client Credentials ─────────────────────────────────────────────────────

    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    @Builder.Default
    private String clientId = UUID.randomUUID().toString();

    @Column(name = "client_secret", nullable = false, length = 255)
    private String clientSecret;   // stored hashed

    @Column(name = "application_code", nullable = false, unique = true, length = 50)
    private String applicationCode;

    @Column(name = "create_date", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createDate = LocalDateTime.now();

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // ── Roles & Status ─────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** Checks whether the client credentials have expired */
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }
}
