package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.configs.SecurityConfig;
import com.gustavomp.devtrackerapi.dtos.requests.user.LoginRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.user.LoginResponseDto;
import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.repositories.UserRepository;
import com.gustavomp.devtrackerapi.services.authentication.TokenService;
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

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        // DEV
        var user = new User();
        user.setUsername("dev");
        user.setPassword((new SecurityConfig()).passwordEncoder().encode("dev"));
        userRepository.save(user);
        // DEV

        var token = new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password());

        var authentication = authenticationManager.authenticate(token);
        String accessToken = tokenService.generateToken((User) Objects.requireNonNull(authentication.getPrincipal()));

        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }

}
