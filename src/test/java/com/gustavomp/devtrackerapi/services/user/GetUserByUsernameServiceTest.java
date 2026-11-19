package com.gustavomp.devtrackerapi.services.user;

import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

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
@DisplayName("Get User By Username Service")
public class GetUserByUsernameServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserByUsernameService getUserByUsernameService;

    @Test
    @DisplayName("Get User By Username | User exists | Returns the user")
    void execute_whenUserExists_shouldReturnUser() {
        // Arrange
        User user = new User();
        when(userRepository.findByUsername("ana")).thenReturn(Optional.of(user));

        // Act
        User actualUser = getUserByUsernameService.execute("ana");

        // Assert
        assertSame(user, actualUser);
    }

    @Test
    @DisplayName("Get User By Username | User does not exist | Throws EntityNotFoundException")
    void execute_whenUserDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("ana")).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(EntityNotFoundException.class,
                () -> getUserByUsernameService.execute("ana"));
        assertEquals("User not found", exception.getMessage());
    }
}
