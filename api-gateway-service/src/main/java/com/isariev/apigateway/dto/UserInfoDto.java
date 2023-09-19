package com.isariev.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfoDto(
        String sub,

        @JsonProperty("email_verified")
        boolean emailVerified,

        @JsonProperty("preferred_username")
        String preferredUsername,

        String email
) {
}
