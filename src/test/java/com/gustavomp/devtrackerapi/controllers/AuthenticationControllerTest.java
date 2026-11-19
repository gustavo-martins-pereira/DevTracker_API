package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.user.LoginUserRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.user.RegisterUserRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.user.LoginResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.exceptions.GlobalExceptionHandler;
import com.gustavomp.devtrackerapi.services.user.LoginUserService;
import com.gustavomp.devtrackerapi.services.user.RegisterUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Controller")
public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private RegisterUserService registerUserService;

    @Mock
    private LoginUserService loginUserService;

    private MockMvc mockMvc;

    private static final String CREDENTIALS_JSON = """
            {
                "username": "ana",
                "password": "test-password"
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Register User | Valid request | Returns 204 No Content")
    void registerUser_whenRequestIsValid_shouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(CREDENTIALS_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(registerUserService).execute(new RegisterUserRequestDto("ana", "test-password"));
    }

    @Test
    @DisplayName("Register User | Username already taken | Returns 409 Conflict")
    void registerUser_whenUsernameIsTaken_shouldReturnConflict() throws Exception {
        // Arrange
        var request = new RegisterUserRequestDto("ana", "test-password");
        doThrow(new EntityAlreadyExistsException("Username already taken"))
                .when(registerUserService).execute(request);

        // Act & Assert
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(CREDENTIALS_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Username already taken"));
    }

    @Test
    @DisplayName("Login User | Valid credentials | Returns 200 and the token")
    void loginUser_whenCredentialsAreValid_shouldReturnToken() throws Exception {
        // Arrange
        var request = new LoginUserRequestDto("ana", "test-password");
        when(loginUserService.execute(request)).thenReturn(new LoginResponseDto("test-token"));

        // Act & Assert
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(CREDENTIALS_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));

        verify(loginUserService).execute(request);
    }

    @Test
    @DisplayName("Login User | Invalid credentials | Returns 401 Unauthorized")
    void loginUser_whenCredentialsAreInvalid_shouldReturnUnauthorized() throws Exception {
        // Arrange
        var request = new LoginUserRequestDto("ana", "test-password");
        when(loginUserService.execute(request)).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(CREDENTIALS_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @ParameterizedTest
    @CsvSource({
            "/auth/register, username",
            "/auth/register, password",
            "/auth/login, username",
            "/auth/login, password"
    })
    @DisplayName("Authentication | Blank credential field | Returns 400 Bad Request")
    void authenticate_whenCredentialFieldIsBlank_shouldReturnBadRequest(String endpoint, String field)
            throws Exception {
        // Arrange
        String invalidJson = CREDENTIALS_JSON.replace(
                field.equals("username") ? "\"ana\"" : "\"test-password\"", "\" \"");

        // Act & Assert
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error." + field).exists());

        verifyNoInteractions(registerUserService, loginUserService);
    }
}
