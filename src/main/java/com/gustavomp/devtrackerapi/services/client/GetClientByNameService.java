package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.responses.client.GetClientByNameResponseDto;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetClientByNameService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public GetClientByNameResponseDto execute(String name) {
        Optional<Client> optionalClient = clientRepository.findByNameAndActiveIsTrue(name);

        Client client = optionalClient.orElseThrow(() -> new EntityNotFoundException("Client with name: '" + name +
                "' not found"));

        return clientMapper.toGetClientByNameResponseDto(client);
    }

}
