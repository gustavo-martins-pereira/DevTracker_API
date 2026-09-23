package com.gustavomp.devtrackerapi.dtos.requests.financialtransaction;

import com.gustavomp.devtrackerapi.models.enums.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Create_FT_RequestDto(
        @NotBlank(message = "The 'description' couldn't be blank")
        String description,

        @NotNull(message = "The 'amount' is required")
        @Positive(message = "The 'amount' must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "The 'amount' format must be invalid (e.g. 1000.00)")
        BigDecimal amount,

        @NotNull(message = "The 'date' is required")
        LocalDate date,

        @NotNull(message = "The 'transactionType' is required")
        TransactionType transactionType,

        @NotNull(message = "The 'projectId' is required")
        @Positive(message = "The 'projectId' must be a positive ID")
        Long projectId
) {}
