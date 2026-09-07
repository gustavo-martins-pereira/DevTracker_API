package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateClientService {

    @Autowired
    private ClientRepository clientRepository;

    public CreateClientResponseDto execute(CreateClientRequestDto createClientRequestDto) {
        Optional<Client> client = clientRepository.findByName(createClientRequestDto.name());
        if (client.isPresent()) {
            throw new EntityAlreadyExistsException(
                    "A client with the name '" + createClientRequestDto.name() + "' already exists."
            );
        }

        Client newClient = new Client();
        newClient.setName(createClientRequestDto.name());
        newClient.setEmail(createClientRequestDto.email());
        newClient.setPhone(createClientRequestDto.phone());
        newClient.setActive(createClientRequestDto.active());

        Client savedClient = clientRepository.save(newClient);

        return new CreateClientResponseDto(savedClient);
    }

}
