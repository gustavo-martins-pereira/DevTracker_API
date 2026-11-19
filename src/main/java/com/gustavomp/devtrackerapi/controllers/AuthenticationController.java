package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.user.LoginUserRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.user.RegisterUserRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.user.LoginResponseDto;
import com.gustavomp.devtrackerapi.services.user.LoginUserService;
import com.gustavomp.devtrackerapi.services.user.RegisterUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final RegisterUserService registerUserService;
    private final LoginUserService loginUserService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterUserRequestDto registerUserRequestDto) {
        registerUserService.execute(registerUserRequestDto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody @Valid LoginUserRequestDto loginUserRequestDto) {
        LoginResponseDto loginResponseDto = loginUserService.execute(loginUserRequestDto);

        return ResponseEntity.ok(loginResponseDto);
    }

}
