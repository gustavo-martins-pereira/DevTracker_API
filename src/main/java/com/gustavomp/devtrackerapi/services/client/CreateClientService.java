package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.client.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public CreateClientResponseDto execute(CreateClientRequestDto createClientRequestDto) {
        Optional<Client> existingClient = clientRepository.findByNameOrEmailOrPhone(
                createClientRequestDto.name(),
                createClientRequestDto.email(),
                createClientRequestDto.phone()
        );
        if (existingClient.isPresent()) {
            throw new EntityAlreadyExistsException(
                    "A client with the 'name', 'email' or 'phone' already exists."
            );
        }

        Client client = clientMapper.createClientRequestDtoToEntity(createClientRequestDto);

        Client savedClient = clientRepository.save(client);

        return clientMapper.toCreateClientResponseDto(savedClient);
    }

}
