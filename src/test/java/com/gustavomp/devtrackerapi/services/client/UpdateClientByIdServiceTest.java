package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.client.UpdateClientByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.UpdateClientByIdResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
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

    @Test
    @DisplayName("Update Client | Successfully | Saves and returns the updated client")
    void execute_whenClientIsFounded_shouldReturnUpdatedClient() {
        // Arrange
        UpdateClientByIdRequestDto requestDto = new UpdateClientByIdRequestDto(
                "Bob",
                "bob@example.com",
                "87654321"
        );

        Client oldClient = new Client(1L, "Ana", "ana@example.com", "12345678", true);
        when(clientRepository.findByIdAndActiveIsTrue(1L)).thenReturn(Optional.of(oldClient));

        when(clientRepository.findByNameOrEmailOrPhone(requestDto.name(), requestDto.email(), requestDto.phone()))
                .thenReturn(Optional.empty());

        Client newClient = new Client(1L, "Bob", "bob@example.com", "87654321", true);
        when(clientMapper.toEntity(requestDto, oldClient)).thenReturn(newClient);

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
        var actualResponse = updateClientByIdService.execute(1L, requestDto);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(clientMapper).toEntity(requestDto, oldClient);
        verify(clientRepository).save(newClient);
        verify(clientMapper).toUpdateClientByIdResponseDto(updatedClient);
    }

    @Test
    @DisplayName("Update Client | Client not found | Throws EntityNotFoundException")
    void execute_whenClientIsNotFounded_shouldThrowEntityNotFoundException() {
        // Arrange
        UpdateClientByIdRequestDto requestDto = new UpdateClientByIdRequestDto(
                "Bob",
                "bob@example.com",
                "87654321"
        );

        when(clientRepository.findByIdAndActiveIsTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> updateClientByIdService.execute(1L, requestDto));

        verify(clientRepository).findByIdAndActiveIsTrue(1L);
        verify(clientRepository, never()).save(any());
        verifyNoInteractions(clientMapper);
    }

    @Test
    @DisplayName("Update Client | Other Client with same name/email/phone  | Throws EntityAlreadyExistsException")
    void execute_whenOtherClientIsFounded_shouldThrowEntityAlreadyExistsException() {
        // Arrange
        UpdateClientByIdRequestDto requestDto = new UpdateClientByIdRequestDto(
                "Bob",
                "bob@example.com",
                "87654321"
        );

        Client oldClient = new Client(1L, "Ana", "ana@example.com", "12345678", true);
        when(clientRepository.findByIdAndActiveIsTrue(1L)).thenReturn(Optional.of(oldClient));

        Client existingClient = new Client(2L, "Bob", "duplicate@example.com", "23456789", true);
        when(clientRepository.findByNameOrEmailOrPhone(requestDto.name(), requestDto.email(), requestDto.phone()))
                .thenReturn(Optional.of(existingClient));

        // Act & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> updateClientByIdService.execute(1L, requestDto));

        verify(clientRepository).findByIdAndActiveIsTrue(1L);
        verify(clientRepository).findByNameOrEmailOrPhone(requestDto.name(), requestDto.email(), requestDto.phone());
        verify(clientRepository, never()).save(any());
        verifyNoInteractions(clientMapper);
    }

}
