package com.gustavomp.devtrackerapi.services.user;

import com.gustavomp.devtrackerapi.dtos.requests.user.LoginRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.user.LoginResponseDto;
import com.gustavomp.devtrackerapi.mappers.UserMapper;
import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.services.authentication.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoginUserService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    public LoginResponseDto execute(LoginRequestDto loginRequestDto) {
        var authentication = new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password());

        Authentication authenticatedUser = authenticationManager.authenticate(authentication);
        String token = tokenService.generateToken((User) Objects.requireNonNull(authenticatedUser.getPrincipal()));

        return userMapper.toLoginResponseDto(token);
    }

}
