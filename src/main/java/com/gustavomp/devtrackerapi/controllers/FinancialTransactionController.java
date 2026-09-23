package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.Create_FT_RequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Create_FT_ResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAll_FT_ByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.services.financialtransaction.Create_FT_Service;
import com.gustavomp.devtrackerapi.services.financialtransaction.GetAll_FT_ByProjectIdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financial-transactions")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final Create_FT_Service createFTService;
    private final GetAll_FT_ByProjectIdService getAllFTByProjectIdService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<Create_FT_ResponseDto> createFinancialTransaction(@RequestBody @Valid Create_FT_RequestDto createFTRequestDto) {
        Create_FT_ResponseDto createFTResponseDto = createFTService.execute(createFTRequestDto);

        return new ResponseEntity<>(createFTResponseDto, HttpStatus.CREATED);
    }

    /* ---------- GET ---------- */
    @GetMapping(params = "projectId")
    public ResponseEntity<List<GetAll_FT_ByProjectIdResponseDto>> getAllFinancialTransactionByProjectId(@RequestParam Long projectId) {
        List<GetAll_FT_ByProjectIdResponseDto> financialTransactions = getAllFTByProjectIdService.execute(projectId);

        return new ResponseEntity<>(financialTransactions, HttpStatus.OK);
    }

}
