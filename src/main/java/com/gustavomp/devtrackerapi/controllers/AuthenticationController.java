package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.user.LoginRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.user.RegisterRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.user.LoginResponseDto;
import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.services.authentication.TokenService;
import com.gustavomp.devtrackerapi.services.user.RegisterUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final RegisterUserService registerUserService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        registerUserService.execute(registerRequestDto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        var token = new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password());

        var authentication = authenticationManager.authenticate(token);
        String accessToken = tokenService.generateToken((User) Objects.requireNonNull(authentication.getPrincipal()));

        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }

}
