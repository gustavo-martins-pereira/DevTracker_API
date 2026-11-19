package com.gustavomp.devtrackerapi.dtos.requests.user;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDto(
        @NotBlank
        String username,

        @NotBlank
        String password
) {}
