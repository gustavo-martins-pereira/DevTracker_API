package com.gustavomp.devtrackerapi.dtos.responses;

import com.gustavomp.devtrackerapi.models.Client;

public record GetClientByNameResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        Boolean active
) {
    public GetClientByNameResponseDto(Client client) {
        this(client.getId(), client.getName(), client.getEmail(), client.getPhone(), client.getActive());
    }
}
