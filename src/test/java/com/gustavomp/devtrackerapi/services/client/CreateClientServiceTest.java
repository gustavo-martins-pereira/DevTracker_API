package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.client.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private CreateClientService createClientService;

    // GIVEN No matching clients exists
    // WHEN I attempt to create a client
    // THEN The client is saved & it's response is returned
    @Test
    void accept_client_creation_when_no_matching_client_exists() {
        // Arrange
        CreateClientRequestDto request = new CreateClientRequestDto(
                "Ana",
                "ana@example.com",
                "12345678",
                true
        );

        when(clientRepository.findByNameOrEmailOrPhone(
                request.name(),
                request.email(),
                request.phone()
        )).thenReturn(Optional.empty());

        Client clientToSave = new Client();
        Client savedClient = new Client();
        savedClient.setId(1L);

        when(clientMapper.toEntity(request)).thenReturn(clientToSave);
        when(clientRepository.save(clientToSave)).thenReturn(savedClient);

        CreateClientResponseDto expectedResponse = new CreateClientResponseDto(
                1L,
                request.name(),
                request.email(),
                request.phone(),
                request.active()
        );

        when(clientMapper.toCreateClientResponseDto(savedClient)).thenReturn(expectedResponse);

        // Act
        var actualResponse = createClientService.execute(request);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(clientRepository).save(clientToSave);
    }

    // GIVEN The repository finds an existing client
    // WHEN I attempt to create a client
    // THEN The service rejects the request and does not save anything.
    @Test
    void rejects_client_creation_when_client_already_exists() {
        // Arrange
        CreateClientRequestDto request = new CreateClientRequestDto(
                "Ana",
                "ana@example.com",
                "12345678",
                true
        );

        Client existingClient = new Client();

        when(clientRepository.findByNameOrEmailOrPhone(
                request.name(),
                request.email(),
                request.phone()
        )).thenReturn(Optional.of(existingClient));

        // Act & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> createClientService.execute(request));
        verify(clientRepository, never()).save(any());
    }

}
