package com.gustavomp.devtrackerapi.dtos.responses.client;

public record CreateClientResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        Boolean active
) {}
