package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.configs.SecurityConfig;
import com.gustavomp.devtrackerapi.dtos.requests.user.LoginRequestDto;
import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        // DEV
        var user = new User();
        user.setUsername("dev");
        user.setPassword((new SecurityConfig()).passwordEncoder().encode("dev"));
        userRepository.save(user);
        // DEV

        var credentials = new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password());

        authenticationManager.authenticate(credentials);

        return ResponseEntity.ok().build();
    }

}
