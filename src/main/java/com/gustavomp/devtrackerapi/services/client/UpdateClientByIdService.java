package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.UpdateClientByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.UpdateClientByIdResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UpdateClientByIdService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    public UpdateClientByIdResponseDto execute(Long id, UpdateClientByIdRequestDto updateClientByIdRequestDto) {
        Optional<Client> optionalClient = clientRepository.findByIdAndActiveIsTrue(id);
        if (optionalClient.isEmpty()) throw new EntityNotFoundException("Client with id " + id + " not found");

        Optional<Client> existingClient = clientRepository.findByNameOrEmailOrPhone(
                updateClientByIdRequestDto.name(),
                updateClientByIdRequestDto.email(),
                updateClientByIdRequestDto.phone());
        if (existingClient.isPresent() && !Objects.equals(existingClient.get().getId(), id)) {
            throw new EntityAlreadyExistsException("Client with 'name' or 'email' or 'phone' already exists");
        }

        Client client = optionalClient.get();
        client.setName(updateClientByIdRequestDto.name());
        client.setEmail(updateClientByIdRequestDto.email());
        client.setPhone(updateClientByIdRequestDto.phone());

        Client updatedClient = clientRepository.save(client);

        return clientMapper.toUpdateClientByIdResponseDto(updatedClient);
    }

}
