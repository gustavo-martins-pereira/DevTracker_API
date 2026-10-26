package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Client by Id Service")
public class DeleteClientByIdServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private DeleteClientByIdService deleteClientByIdService;

    @Test
    @DisplayName("Delete Client | Existing client | Deactivates the client")
    void execute_whenClientExists_shouldDeleteClient() {
        // Arrange
        Client client = new Client(1L, "Ana", "ana@example.com", "12345678", true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // Act
        deleteClientByIdService.execute(1L);

        // Assert
        assertFalse(client.getActive());

        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("Delete Client | Client not found | Throws EntityNotFoundException")
    void execute_whenClientDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> deleteClientByIdService.execute(1L));

        verify(clientRepository, never()).save(any());
    }

}
