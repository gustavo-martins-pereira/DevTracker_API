package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.client.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Create Client Service")
public class CreateClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private CreateClientService createClientService;

    /**
     * Verifies successful client creation.
     *
     * <p><b>GIVEN:</b> No client matches the requested name, email, or phone.
     * <br><b>WHEN:</b> The service executes the creation request.
     * <br><b>THEN:</b> The client is saved and its response is returned.
     */
    @Test
    @DisplayName("Create Client | No matching client | Saves and returns the saved client")
    void execute_whenNoMatchingClientExists_shouldReturnCreatedClient() {
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

        verify(clientMapper).toEntity(request);
        verify(clientMapper).toCreateClientResponseDto(savedClient);
        verify(clientRepository).save(clientToSave);
    }

    /**
     * Verifies rejection of a duplicate client.
     *
     * <p><b>GIVEN:</b> A client matches the requested name, email, or phone.
     * <br><b>WHEN:</b> The service executes the creation request.
     * <br><b>THEN:</b> EntityAlreadyExistsException is thrown,
     * and neither mapping nor saving occurs.
     */
    @Test
    @DisplayName("Create Client | Matching client exists | Returns EntityAlreadyExistsException ")
    void execute_whenMatchingClientExists_shouldThrowEntityAlreadyExistsException() {
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

        verifyNoInteractions(clientMapper);

        verify(clientRepository, never()).save(any());
    }

}
