package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteClientByIdService {

    @Autowired
    private ClientRepository clientRepository;

    public void execute(Long id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isEmpty()) throw new EntityNotFoundException("Client with id " + id + " not found");

        Client client = clientOptional.get();
        client.setActive(false);

        clientRepository.save(client);
    }

}
