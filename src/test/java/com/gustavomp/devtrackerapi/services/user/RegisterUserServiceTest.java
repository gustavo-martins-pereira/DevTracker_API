package com.gustavomp.devtrackerapi.services.user;

import com.gustavomp.devtrackerapi.dtos.requests.user.RegisterUserRequestDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.UserMapper;
import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.repositories.UserRepository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Register User Service")
public class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RegisterUserService registerUserService;

    private final RegisterUserRequestDto request = new RegisterUserRequestDto("ana", "test-password");

    @Test
    @DisplayName("Register User | Username available | Saves the mapped user")
    void execute_whenUsernameIsAvailable_shouldSaveUser() {
        // Arrange
        User user = new User();
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);

        // Act
        registerUserService.execute(request);

        // Assert
        verify(userMapper).toEntity(request);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Register User | Username already taken | Throws EntityAlreadyExistsException")
    void execute_whenUsernameIsTaken_shouldThrowEntityAlreadyExistsException() {
        // Arrange
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(new User()));

        // Act & Assert
        var exception = assertThrows(EntityAlreadyExistsException.class,
                () -> registerUserService.execute(request));
        assertEquals("Username already taken", exception.getMessage());

        verifyNoInteractions(userMapper);
        verify(userRepository, never()).save(any());
    }
}
