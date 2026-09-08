package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.CreateClientRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.UpdateClientByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.CreateClientResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.GetAllClientsResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.GetClientByNameResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.UpdateClientByIdResponseDto;
import com.gustavomp.devtrackerapi.services.client.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private CreateClientService createClientService;

    @Autowired
    private GetAllClientsService getAllClientsService;

    @Autowired
    private GetClientByNameService getClientByNameService;

    @Autowired
    private UpdateClientByIdService updateClientByIdService;

    @Autowired
    private DeleteClientByIdService deleteClientByIdService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateClientResponseDto> createClient(@RequestBody @Valid CreateClientRequestDto createClientRequestDto) {
        CreateClientResponseDto createClientResponseDto = createClientService.execute(createClientRequestDto);

        return new ResponseEntity<>(createClientResponseDto, HttpStatus.CREATED);
    }

    /* ---------- GET ---------- */
    @GetMapping
    public ResponseEntity<List<GetAllClientsResponseDto>> getAllClients() {
        List<GetAllClientsResponseDto> getAllClientsResponseDto = getAllClientsService.execute();

        return ResponseEntity.ok(getAllClientsResponseDto);
    }

    @GetMapping(params = "name")
    public ResponseEntity<GetClientByNameResponseDto> getClientByName(@RequestParam String name) {
        GetClientByNameResponseDto getClientByNameResponseDto = getClientByNameService.execute(name);

        return ResponseEntity.ok(getClientByNameResponseDto);
    }

    /* ---------- PUT ---------- */
    @PutMapping(params = "id")
    public ResponseEntity<UpdateClientByIdResponseDto> updateClientById(@RequestParam Long id, @RequestBody @Valid UpdateClientByIdRequestDto updateClientByIdRequestDto) {
        UpdateClientByIdResponseDto updateClientByIdResponseDto = updateClientByIdService.execute(id,updateClientByIdRequestDto);

        return ResponseEntity.ok(updateClientByIdResponseDto);
    }

    /* ---------- DELETE ---------- */
    @DeleteMapping(params = "id")
    public ResponseEntity<Void> deleteClientById(@RequestParam Long id) {
        deleteClientByIdService.execute(id);

        return ResponseEntity.noContent().build();
    }

}
