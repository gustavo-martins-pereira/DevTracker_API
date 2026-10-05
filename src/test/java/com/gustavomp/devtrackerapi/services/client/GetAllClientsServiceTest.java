package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.responses.client.GetAllClientsResponseDto;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get All Clients Service")
public class GetAllClientsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private GetAllClientsService getAllClientsService;

    @Test
    @DisplayName("Get All Clients | With Active/Innactive clients | Returns the active clients")
    void execute_whenClientsExists_shouldReturnClients() {
        // Arrange
        Client activeClient = new Client(1L, "Ana", "ana@example.com", "12345678", true);
        Client inactiveClient = new Client(2L, "Bob", "bob@example.com", "87654321", false);

        when(clientRepository.findAll()).thenReturn(List.of(activeClient, inactiveClient));

        GetAllClientsResponseDto expectedResponse = new GetAllClientsResponseDto(
                1L,
                "Ana",
                "ana@example.com",
                "12345678",
                true
        );

        when(clientMapper.toAllClientsResponseDto(activeClient)).thenReturn(expectedResponse);

        // Act
        var actualResponse = getAllClientsService.execute();

        // Assert
        assertEquals(List.of(expectedResponse), actualResponse);

        verify(clientMapper).toAllClientsResponseDto(activeClient);
        verify(clientMapper, never()).toAllClientsResponseDto(inactiveClient);
    }

    @Test
    @DisplayName("Get All Clients | No clients exists | Returns an empty clients list")
    void execute_whenClientsNotExists_shouldReturnEmptyList() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(List.of());

        // Act
        var actualResponse = getAllClientsService.execute();

        // Assert
        assertTrue(actualResponse.isEmpty());

        verifyNoInteractions(clientMapper);
    }

}
