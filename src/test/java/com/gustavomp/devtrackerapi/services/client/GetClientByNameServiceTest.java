package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.responses.client.GetClientByNameResponseDto;
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
@DisplayName("Get Client by Name Service")
public class GetClientByNameServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private GetClientByNameService getClientByNameService;

    @Test
    @DisplayName("Get Client by Name | Existing client with given name | Returns the client")
    void execute_whenSearchByName_shouldReturnClient() {
        // Arrange
        String searchedName = "Ana";

        Client existingClient = new Client(1L, "Ana", "ana@example.com", "12345678", true);
        when(clientRepository.findByNameAndActiveIsTrue(searchedName)).thenReturn(Optional.of(existingClient));

        GetClientByNameResponseDto expectedResponse = new GetClientByNameResponseDto(
                1L,
                "Ana",
                "ana@example.com",
                "12345678",
                true
        );
        when(clientMapper.toGetClientByNameResponseDto(existingClient))
                .thenReturn(expectedResponse);

        // Act
        var actualResponse = getClientByNameService.execute(searchedName);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(clientRepository).findByNameAndActiveIsTrue(searchedName);
        verify(clientMapper).toGetClientByNameResponseDto(existingClient);
    }

    @Test
    @DisplayName("Get Client by Name | No clients existing with the searched name | Returns EntityNotFoundException")
    void execute_whenSearchByUnexistedName_shouldThrowsEntityNotFoundException() {
        // Arrange
        String searchedName = "Bob";

        when(clientRepository.findByNameAndActiveIsTrue(searchedName)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> getClientByNameService.execute(searchedName));

        verify(clientRepository).findByNameAndActiveIsTrue(searchedName);
        verifyNoInteractions(clientMapper);
    }

}
