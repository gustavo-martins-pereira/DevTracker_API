package com.gustavomp.devtrackerapi.dtos.responses.client;

public record UpdateClientByIdResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        Boolean active
) {}
