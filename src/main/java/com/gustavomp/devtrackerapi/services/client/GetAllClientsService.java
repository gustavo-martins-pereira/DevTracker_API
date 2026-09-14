package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.responses.client.GetAllClientsResponseDto;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllClientsService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public List<GetAllClientsResponseDto> execute() {
        return clientRepository.findAll()
                .stream()
                .filter(Client::getActive)
                .map(clientMapper::toAllClientsResponseDto)
                .toList();
    }

}
