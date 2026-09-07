package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.GetClientByNameResponseDto;
import com.gustavomp.devtrackerapi.services.client.CreateClientService;
import com.gustavomp.devtrackerapi.services.client.GetClientByNameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private CreateClientService createClientService;

    @Autowired
    private GetClientByNameService getClientByNameService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateClientResponseDto> createClient(@RequestBody @Valid CreateClientRequestDto createClientRequestDto) {
        CreateClientResponseDto createClientResponseDto = createClientService.execute(createClientRequestDto);

        return new ResponseEntity<>(createClientResponseDto, HttpStatus.CREATED);
    }

    /* ---------- GET ---------- */
    @GetMapping
    public ResponseEntity<GetClientByNameResponseDto> getClientByName(@RequestParam String name) {
        GetClientByNameResponseDto getClientByNameResponseDto = getClientByNameService.execute(name);

        return ResponseEntity.ok(getClientByNameResponseDto);
    }

}
