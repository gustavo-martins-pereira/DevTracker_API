package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.responses.GetAllClientsResponseDto;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllClientsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    public List<GetAllClientsResponseDto> execute() {
        return clientRepository.findAll()
                .stream()
                .map(client -> clientMapper.toAllClientsResponseDto(client))
                .toList();
    }

}
