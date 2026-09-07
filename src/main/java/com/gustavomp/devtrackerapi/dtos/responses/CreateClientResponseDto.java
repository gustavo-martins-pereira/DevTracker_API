package com.gustavomp.devtrackerapi.dtos.responses;

import com.gustavomp.devtrackerapi.models.Client;

public record CreateClientResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        Boolean active
) {
    public CreateClientResponseDto(Client client) {
        this(client.getId(), client.getName(), client.getEmail(), client.getPhone(), client.getActive());
    }
}
