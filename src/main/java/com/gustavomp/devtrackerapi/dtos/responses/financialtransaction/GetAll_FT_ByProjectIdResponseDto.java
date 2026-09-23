package com.gustavomp.devtrackerapi.dtos.responses.financialtransaction;

import com.gustavomp.devtrackerapi.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetAll_FT_ByProjectIdResponseDto(
        Long id,
        String description,
        BigDecimal amount,
        LocalDate date,
        TransactionType transactionType
) {}
