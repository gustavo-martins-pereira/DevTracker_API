package com.gustavomp.devtrackerapi.dtos.responses.financialtransaction;

import com.gustavomp.devtrackerapi.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateFinancialTransactionResponseDto(
        Long id,
        String description,
        BigDecimal amount,
        LocalDate date,
        TransactionType transactionType,
        Long projectId
) {}
