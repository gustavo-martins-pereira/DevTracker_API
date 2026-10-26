package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.client.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.client.UpdateClientByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.client.*;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.exceptions.GlobalExceptionHandler;
import com.gustavomp.devtrackerapi.services.client.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Client Controller")
public class ClientControllerTest {

    @InjectMocks
    private ClientController clientController;

    @Mock
    private CreateClientService createClientService;

    @Mock
    private GetAllClientsService getAllClientsService;

    @Mock
    private GetClientByNameService getClientByNameService;

    @Mock
    private UpdateClientByIdService updateClientByIdService;

    @Mock
    private DeleteClientByIdService deleteClientByIdService;

    private MockMvc mockMvc;

    private static final String clientNotFoundMessage = "Client not found";

    private static final String clientAlreadyExistsMessage = "Client already exists";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /* ---------- Create Client ---------- */
    private static final String CREATE_JSON = """
            {
                "name":"Ana",
                "email":"ana@example.com",
                "phone":"12345678",
                "active":true
            }
            """;

    @Test
    @DisplayName("Create Client | Valid request | Returns 201 and created client")
    void createClient_whenValidRequest_shouldReturnCreatedClient() throws Exception {
        // Arrange
        var request = new CreateClientRequestDto("Ana", "ana@example.com", "12345678", true);
        var response = new CreateClientResponseDto(1L, "Ana", "ana@example.com", "12345678", true);
        when(createClientService.execute(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON).content(CREATE_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                        "id":1,
                        "name":"Ana",
                        "email":"ana@example.com",
                        "phone":"12345678",
                        "active":true
                        }
                        """));

        verify(createClientService).execute(request);
    }

    @Test
    @DisplayName("Create Client | Client already exists | Throws EntityAlreadyExistsException and 409")
    void createClient_whenClientAlreadyExists_shouldReturnConflict() throws Exception {
        // Arrange
        var request = new CreateClientRequestDto("Ana", "ana@example.com", "12345678", true);
        when(createClientService.execute(request)).thenThrow(new EntityAlreadyExistsException(clientAlreadyExistsMessage));

        // Act & Assert
        mockMvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON).content(CREATE_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value(clientAlreadyExistsMessage));

        verify(createClientService).execute(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"\"}",
            "{\"name\":\"An\"}",
            "{\"email\":\"invalid\"}",
            "{\"phone\":\"123\"}",
            "{\"active\":null}"
    })
    @DisplayName("Create Client | Invalid Request params | Throws BadRequestException and 400")
    void createClient_whenInvalidRequest_shouldReturnBadRequest(String invalidField) throws Exception {
        // Arrange
        // Replace one field at a time to exercise each validation constraint independently.
        String field = invalidField.split(":")[0];
        field = field.replace("{", "").replace("\"", "");

        // Remove the surrounding braces: {"name":"An"} becomes "name":"An".
        String replacement = invalidField.substring(1, invalidField.length() - 1);

        // Match the selected field and its existing string or boolean value.
        String fieldPattern = "\"" + field + "\":\\s*(\"[^\"]*\"|true|false|null)";
        String invalidJson = CREATE_JSON.replaceFirst(
                fieldPattern,
                Matcher.quoteReplacement(replacement)
        );
        assertNotEquals(CREATE_JSON, invalidJson, "The test must replace the selected field");

        // Act & Assert
        mockMvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error." + field).isNotEmpty());

        verifyNoInteractions(createClientService);
    }

    @Test
    @DisplayName("Create Client | Malformed JSON | Returns 400 Bad Request")
    void createClient_whenMalformedJson_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON).content("{invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.request").value("Invalid JSON input format"));

        verifyNoInteractions(createClientService);
    }

    /* ---------- Get All Clients ---------- */
    @Test
    @DisplayName("Get All Clients | Clients exist | Returns 200 and clients")
    void getAllClients_whenClientsExist_shouldReturnClients() throws Exception {
        // Arrange
        var response = new GetAllClientsResponseDto(1L, "Ana", "ana@example.com", "12345678", true);
        when(getAllClientsService.execute()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id":1,"name":"Ana","email":"ana@example.com","phone":"12345678","active":true}]
                        """));

        verify(getAllClientsService).execute();
    }

    @Test
    @DisplayName("Get All Clients | No clients exist | Returns 200 and empty list")
    void getAllClients_whenNoClientsExist_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(getAllClientsService.execute()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(getAllClientsService).execute();
    }

    /* ---------- Get Client by Name ---------- */
    @Test
    @DisplayName("Get Client by Name | Client exists | Returns 200 and client")
    void getClientByName_whenClientExists_shouldReturnClient() throws Exception {
        // Arrange
        var response = new GetClientByNameResponseDto(1L, "Ana", "ana@example.com", "12345678", true);
        when(getClientByNameService.execute("Ana")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/clients").param("name", "Ana"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id":1,"name":"Ana","email":"ana@example.com","phone":"12345678","active":true}
                        """));

        verify(getClientByNameService).execute("Ana");
    }

    @Test
    @DisplayName("Get Client by Name | Client does not exist | Returns 404 Not Found")
    void getClientByName_whenClientDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        when(getClientByNameService.execute("Ana")).thenThrow(new EntityNotFoundException(clientNotFoundMessage));

        // Act & Assert
        mockMvc.perform(get("/clients").param("name", "Ana"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(clientNotFoundMessage));

        verify(getClientByNameService).execute("Ana");
    }

    /* ---------- Update Client by Id ---------- */
    private static final String UPDATE_JSON = """
            {
                "name":"Ana Maria",
                "email":"ana.maria@example.com",
                "phone":"87654321"
            }
            """;

    @Test
    @DisplayName("Update Client by Id | Valid request | Returns 200 and updated client")
    void updateClientById_whenValidRequest_shouldReturnUpdatedClient() throws Exception {
        // Arrange
        var request = new UpdateClientByIdRequestDto("Ana Maria", "ana.maria@example.com", "87654321");
        var response = new UpdateClientByIdResponseDto(1L, "Ana Maria", "ana.maria@example.com", "87654321", true);
        when(updateClientByIdService.execute(1L, request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/clients").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id":1,"name":"Ana Maria","email":"ana.maria@example.com","phone":"87654321","active":true}
                        """));

        verify(updateClientByIdService).execute(1L, request);
    }

    @Test
    @DisplayName("Update Client by Id | Client does not exist | Returns 404 Not Found")
    void updateClientById_whenClientDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        var request = new UpdateClientByIdRequestDto("Ana Maria", "ana.maria@example.com", "87654321");
        when(updateClientByIdService.execute(1L, request)).thenThrow(new EntityNotFoundException(clientNotFoundMessage));

        // Act & Assert
        mockMvc.perform(put("/clients").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(clientNotFoundMessage));

        verify(updateClientByIdService).execute(1L, request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"An\"}",
            "{\"email\":\"invalid\"}",
            "{\"phone\":\"123\"}"
    })
    @DisplayName("Update Client by Id | Invalid request | Returns 400 Bad Request")
    void updateClientById_whenInvalidRequest_shouldReturnBadRequest(String invalidJson) throws Exception {
        // Act & Assert
        mockMvc.perform(put("/clients").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isMap());

        verifyNoInteractions(updateClientByIdService);
    }

    @Test
    @DisplayName("Update Client by Id | Client property already exists | Returns 409 Conflict")
    void updateClientById_whenClientAlreadyExists_shouldReturnConflict() throws Exception {
        // Arrange
        var request = new UpdateClientByIdRequestDto("Ana Maria", "ana.maria@example.com", "87654321");
        when(updateClientByIdService.execute(1L, request))
                .thenThrow(new EntityAlreadyExistsException(clientAlreadyExistsMessage));

        // Act & Assert
        mockMvc.perform(put("/clients").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(clientAlreadyExistsMessage));

        verify(updateClientByIdService).execute(1L, request);
    }

    /* ---------- Delete Client by Id ---------- */
    @Test
    @DisplayName("Delete Client by Id | Client exists | Returns 204 No Content")
    void deleteClientById_whenClientExists_shouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/clients").param("id", "1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(deleteClientByIdService).execute(1L);
    }

    @Test
    @DisplayName("Delete Client by Id | Client does not exist | Returns 404 Not Found")
    void deleteClientById_whenClientDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException(clientNotFoundMessage)).when(deleteClientByIdService).execute(1L);

        // Act & Assert
        mockMvc.perform(delete("/clients").param("id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(clientNotFoundMessage));

        verify(deleteClientByIdService).execute(1L);
    }

    @Test
    @DisplayName("Delete Client by Id | Invalid id | Returns 400 Bad Request")
    void deleteClientById_whenInvalidId_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/clients").param("id", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.id").value("Invalid value 'invalid'"));

        verifyNoInteractions(deleteClientByIdService);
    }
}

