package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.Create_FT_RequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Create_FT_ResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAll_FT_ByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Get_FT_ByIdResponseDto;
import com.gustavomp.devtrackerapi.services.financialtransaction.Create_FT_Service;
import com.gustavomp.devtrackerapi.services.financialtransaction.Delete_FT_ByIdService;
import com.gustavomp.devtrackerapi.services.financialtransaction.GetAll_FT_ByProjectIdService;
import com.gustavomp.devtrackerapi.services.financialtransaction.Get_FT_ByIdService;
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

    private final Create_FT_Service create_FT_Service;
    private final GetAll_FT_ByProjectIdService getAll_FT_ByProjectIdService;
    private final Get_FT_ByIdService get_FT_ByIdService;
    private final Delete_FT_ByIdService delete_FT_ByIdService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<Create_FT_ResponseDto> createFinancialTransaction(@RequestBody @Valid Create_FT_RequestDto createFTRequestDto) {
        Create_FT_ResponseDto createFTResponseDto = create_FT_Service.execute(createFTRequestDto);

        return new ResponseEntity<>(createFTResponseDto, HttpStatus.CREATED);
    }

    /* ---------- GET ---------- */
    @GetMapping(params = "projectId")
    public ResponseEntity<List<GetAll_FT_ByProjectIdResponseDto>> getAllFinancialTransactionByProjectId(@RequestParam Long projectId) {
        List<GetAll_FT_ByProjectIdResponseDto> financialTransactions = getAll_FT_ByProjectIdService.execute(projectId);

        return new ResponseEntity<>(financialTransactions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Get_FT_ByIdResponseDto> getFinancialTransactionById(@PathVariable Long id) {
        Get_FT_ByIdResponseDto getFTByIdResponseDto = get_FT_ByIdService.execute(id);

        return new ResponseEntity<>(getFTByIdResponseDto, HttpStatus.OK);
    }

    /* ---------- DELETE ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinancialTransactionById(@PathVariable Long id) {
        delete_FT_ByIdService.execute(id);

        return ResponseEntity.noContent().build();
    }

}
