package com.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    /** The generated clientId returned at registration */
    @NotBlank(message = "Client ID is required")
    private String clientId;

    /** The generated clientSecret returned at registration */
    @NotBlank(message = "Client secret is required")
    private String clientSecret;

    /** The application code used during registration */
    @NotBlank(message = "Application code is required")
    private String applicationCode;
}
