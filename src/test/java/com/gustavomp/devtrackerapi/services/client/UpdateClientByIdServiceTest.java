package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.client.UpdateClientByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.UpdateClientByIdResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Client by Id Service")
public class UpdateClientByIdServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private UpdateClientByIdService updateClientByIdService;

    private UpdateClientByIdRequestDto request;
    private Client oldClient;

    @BeforeEach
    void setUp() {
        request = new UpdateClientByIdRequestDto(
                "Bob",
                "bob@example.com",
                "87654321"
        );
        oldClient = new Client(1L, "Ana", "ana@example.com", "12345678", true);
    }

    @Test
    @DisplayName("Update Client | Existing client | Saves and returns the updated client")
    void execute_whenClientExists_shouldReturnUpdatedClient() {
        // Arrange
        when(clientRepository.findByIdAndActiveIsTrue(1L)).thenReturn(Optional.of(oldClient));

        when(clientRepository.findByNameOrEmailOrPhone(request.name(), request.email(), request.phone()))
                .thenReturn(Optional.empty());

        Client newClient = new Client(1L, "Bob", "bob@example.com", "87654321", true);
        when(clientMapper.toEntity(request, oldClient)).thenReturn(newClient);

        Client updatedClient = new Client(1L, "Bob", "bob@example.com", "87654321", true);
        when(clientRepository.save(newClient)).thenReturn(updatedClient);

        UpdateClientByIdResponseDto expectedResponse = new UpdateClientByIdResponseDto(
                1L,
                "Bob",
                "bob@example.com",
                "87654321",
                true
        );
        when(clientMapper.toUpdateClientByIdResponseDto(updatedClient)).thenReturn(expectedResponse);

        // Act
        var actualResponse = updateClientByIdService.execute(1L, request);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(clientMapper).toEntity(request, oldClient);
        verify(clientRepository).save(newClient);
        verify(clientMapper).toUpdateClientByIdResponseDto(updatedClient);
    }

    @Test
    @DisplayName("Update Client | Client not found | Throws EntityNotFoundException")
    void execute_whenClientDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        when(clientRepository.findByIdAndActiveIsTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> updateClientByIdService.execute(1L, request));

        verify(clientRepository).findByIdAndActiveIsTrue(1L);
        verify(clientRepository, never()).save(any());
        verifyNoInteractions(clientMapper);
    }

    @Test
    @DisplayName("Update Client | Other client with same name/email/phone | Throws EntityAlreadyExistsException")
    void execute_whenOtherClientExists_shouldThrowEntityAlreadyExistsException() {
        // Arrange
        when(clientRepository.findByIdAndActiveIsTrue(1L)).thenReturn(Optional.of(oldClient));

        Client existingClient = new Client(2L, "Bob", "duplicate@example.com", "23456789", true);
        when(clientRepository.findByNameOrEmailOrPhone(request.name(), request.email(), request.phone()))
                .thenReturn(Optional.of(existingClient));

        // Act & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> updateClientByIdService.execute(1L, request));

        verify(clientRepository).findByIdAndActiveIsTrue(1L);
        verify(clientRepository).findByNameOrEmailOrPhone(request.name(), request.email(), request.phone());
        verify(clientRepository, never()).save(any());
        verifyNoInteractions(clientMapper);
    }

}

