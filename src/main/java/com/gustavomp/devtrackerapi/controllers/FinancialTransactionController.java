package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFTRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.CreateFTResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAllFTByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetFTByIdResponseDto;
import com.gustavomp.devtrackerapi.services.financialtransaction.CreateFTService;
import com.gustavomp.devtrackerapi.services.financialtransaction.DeleteFTByIdService;
import com.gustavomp.devtrackerapi.services.financialtransaction.GetAllFTByProjectIdService;
import com.gustavomp.devtrackerapi.services.financialtransaction.GetFTByIdService;
import jakarta.validation.Valid;
import com.gustavomp.devtrackerapi.models.enums.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financial-transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final CreateFTService createFTService;
    private final GetAllFTByProjectIdService getAllFTByProjectIdService;
    private final GetFTByIdService getFTByIdService;
    private final DeleteFTByIdService deleteFTByIdService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateFTResponseDto> createFinancialTransaction(@RequestBody @Valid CreateFTRequestDto createFTRequestDto) {
        CreateFTResponseDto createFTResponseDto = createFTService.execute(createFTRequestDto);

        return new ResponseEntity<>(createFTResponseDto, HttpStatus.CREATED);
    }

    /* ---------- GET ---------- */
    @GetMapping(params = "projectId")
    public ResponseEntity<List<GetAllFTByProjectIdResponseDto>> getAllFinancialTransactionByProjectId(@RequestParam Long projectId, @RequestParam(defaultValue = "BRL") Currency currency) {
        List<GetAllFTByProjectIdResponseDto> financialTransactions = getAllFTByProjectIdService.execute(projectId, currency);

        return new ResponseEntity<>(financialTransactions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetFTByIdResponseDto> getFinancialTransactionById(@PathVariable Long id, @RequestParam(defaultValue = "BRL") Currency currency) {
        GetFTByIdResponseDto getFTByIdResponseDto = getFTByIdService.execute(id, currency);

        return new ResponseEntity<>(getFTByIdResponseDto, HttpStatus.OK);
    }

    /* ---------- DELETE ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialTransactionById(@PathVariable Long id) {
        deleteFTByIdService.execute(id);

        return ResponseEntity.noContent().build();
    }

}

