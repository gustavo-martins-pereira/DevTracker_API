package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.responses.GetClientByNameResponseDto;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetClientByNameService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    public GetClientByNameResponseDto execute(String name) {
        Optional<Client> optionalClient = clientRepository.findByNameAndActiveIsTrue(name);

        Client client = optionalClient.orElseThrow(() -> new EntityNotFoundException("Client with name: '" + name +
                "' not found"));

        return clientMapper.toGetClientByNameResponseDto(client);
    }

}
