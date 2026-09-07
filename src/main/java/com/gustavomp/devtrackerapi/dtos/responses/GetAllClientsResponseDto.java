package com.gustavomp.devtrackerapi.dtos.responses;

import com.gustavomp.devtrackerapi.models.Client;

public record GetAllClientsResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        Boolean active
) {
    public GetAllClientsResponseDto(Client client) {
        this(client.getId(), client.getName(), client.getEmail(), client.getPhone(), client.getActive());
    }
}
