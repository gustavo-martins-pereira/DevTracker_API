package com.gustavomp.devtrackerapi.dtos.requests.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank
        String username,

        @NotBlank
        String password
) {}
