package com.gustavomp.devtrackerapi.dtos.responses.client;

public record GetClientByNameResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        Boolean active
) {}
