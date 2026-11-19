package com.gustavomp.devtrackerapi.services.user;

import com.gustavomp.devtrackerapi.dtos.requests.user.LoginUserRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.user.LoginResponseDto;
import com.gustavomp.devtrackerapi.mappers.UserMapper;
import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.services.authentication.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Login User Service")
public class LoginUserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private LoginUserService loginUserService;

    private final LoginUserRequestDto request = new LoginUserRequestDto("ana", "test-password");

    @Test
    @DisplayName("Login User | Valid credentials | Returns a token")
    void execute_whenCredentialsAreValid_shouldReturnToken() {
        // Arrange
        var authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        User user = new User();
        var authenticatedUser = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        var expectedResponse = new LoginResponseDto("test-token");

        when(authenticationManager.authenticate(authentication)).thenReturn(authenticatedUser);
        when(tokenService.generateToken(user)).thenReturn(expectedResponse.token());
        when(userMapper.toLoginResponseDto(expectedResponse.token())).thenReturn(expectedResponse);

        // Act
        var actualResponse = loginUserService.execute(request);

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Login User | Invalid credentials | Propagates BadCredentialsException")
    void execute_whenCredentialsAreInvalid_shouldThrowBadCredentialsException() {
        // Arrange
        var authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        var expectedException = new BadCredentialsException("Invalid credentials");
        when(authenticationManager.authenticate(authentication)).thenThrow(expectedException);

        // Act & Assert
        var actualException = assertThrows(BadCredentialsException.class,
                () -> loginUserService.execute(request));
        assertSame(expectedException, actualException);

        verifyNoInteractions(tokenService, userMapper);
    }
}
