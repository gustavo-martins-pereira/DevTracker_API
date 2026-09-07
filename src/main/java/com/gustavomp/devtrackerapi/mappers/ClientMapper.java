package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.GetAllClientsResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.GetClientByNameResponseDto;
import com.gustavomp.devtrackerapi.models.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    // Create Client
    Client toEntity(CreateClientRequestDto dto);
    CreateClientResponseDto toCreateClientResponseDto(Client entity);

    // Get All Clients
    GetAllClientsResponseDto toAllClientsResponseDto(Client entity);

    // Get Client by Name
    GetClientByNameResponseDto toGetClientByNameResponseDto(Client entity);

}
