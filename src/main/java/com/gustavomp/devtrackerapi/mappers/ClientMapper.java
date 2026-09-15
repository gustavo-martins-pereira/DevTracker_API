package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.client.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.client.UpdateClientByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.GetAllClientsResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.GetClientByNameResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.UpdateClientByIdResponseDto;
import com.gustavomp.devtrackerapi.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {

    // Create Client
    Client toEntity(CreateClientRequestDto dto);
    CreateClientResponseDto toCreateClientResponseDto(Client entity);

    // Get All Clients
    GetAllClientsResponseDto toAllClientsResponseDto(Client entity);

    // Get Client by Name
    GetClientByNameResponseDto toGetClientByNameResponseDto(Client entity);

    // Update Client by ID
    Client toEntity(UpdateClientByIdRequestDto dto, @MappingTarget Client entity);
    UpdateClientByIdResponseDto toUpdateClientByIdResponseDto(Client entity);

}
