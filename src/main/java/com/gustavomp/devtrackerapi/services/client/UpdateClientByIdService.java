package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.client.UpdateClientByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.UpdateClientByIdResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateClientByIdService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public UpdateClientByIdResponseDto execute(Long id, UpdateClientByIdRequestDto updateClientByIdRequestDto) {
        Optional<Client> oldClient = clientRepository.findByIdAndActiveIsTrue(id);
        if (oldClient.isEmpty()) throw new EntityNotFoundException("Client with id " + id + " not found");

        Optional<Client> existingClient = clientRepository.findByNameOrEmailOrPhone(
                updateClientByIdRequestDto.name(),
                updateClientByIdRequestDto.email(),
                updateClientByIdRequestDto.phone());
        if (existingClient.isPresent() && !Objects.equals(existingClient.get().getId(), id)) {
            throw new EntityAlreadyExistsException("Client with 'name' or 'email' or 'phone' already exists");
        }

        Client client = clientMapper.toEntity(updateClientByIdRequestDto, oldClient.get());

        Client updatedClient = clientRepository.save(client);

        return clientMapper.toUpdateClientByIdResponseDto(updatedClient);
    }

}
