package com.gustavomp.devtrackerapi.services.client;

import com.gustavomp.devtrackerapi.dtos.requests.client.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.GetAllClientsResponseDto;
import com.gustavomp.devtrackerapi.mappers.ClientMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
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
public class GetAllClientsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private GetAllClientsService getAllClientsService;

    /**
     * Verifies successful returned clients.
     *
     * <p><b>GIVEN:</b> Clients exists.
     * <br><b>WHEN:</b> Getting all the clients with <i>getActive</i> is <b>true</b>.
     * <br><b>THEN:</b> The clients are returned.
     */
    @Test
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

    /**
     * Verifies successful returned clients (Empty List).
     *
     * <p><b>GIVEN:</b> Clients doesn't exist.
     * <br><b>WHEN:</b> Getting all the clients with <i>getActive</i> is <b>true</b>.
     * <br><b>THEN:</b> No clients are returned.
     */
    @Test
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
