package com.gustavomp.devtrackerapi.dtos.responses;

public record UpdateClientByIdResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        Boolean active
) {}
