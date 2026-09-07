package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.services.client.CreateClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private CreateClientService createClientService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateClientResponseDto> createClient(@RequestBody @Valid CreateClientRequestDto createClientRequestDto) {
        CreateClientResponseDto createClientResponseDto = createClientService.execute(createClientRequestDto);

        return new ResponseEntity<>(createClientResponseDto, HttpStatus.CREATED);
    }

}
