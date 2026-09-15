package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFinancialTransactionRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.CreateFinancialTransactionResponseDto;
import com.gustavomp.devtrackerapi.services.financialtransaction.CreateFinancialTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/financial-transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final CreateFinancialTransactionService createFinancialTransactionService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateFinancialTransactionResponseDto> createFinancialTransaction(@RequestBody @Valid CreateFinancialTransactionRequestDto createFinancialTransactionRequestDto) {
        CreateFinancialTransactionResponseDto createFinancialTransactionResponseDto = createFinancialTransactionService.execute(createFinancialTransactionRequestDto);

        return new ResponseEntity<>(createFinancialTransactionResponseDto, HttpStatus.CREATED);
    }

}
