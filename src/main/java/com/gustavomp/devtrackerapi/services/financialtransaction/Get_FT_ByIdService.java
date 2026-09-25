package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Get_FT_ByIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.models.enums.Currency;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import com.gustavomp.devtrackerapi.services.awesomeapi.CurrencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.math.BigDecimal;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;

@Service
@RequiredArgsConstructor
public class Get_FT_ByIdService {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialTransactionMapper financialTransactionMapper;
    private final CurrencyService currencyService;

    public Get_FT_ByIdResponseDto execute(Long id) {
        return execute(id, Currency.BRL);
    }

    public Get_FT_ByIdResponseDto execute(Long id, Currency currency) {
        FinancialTransaction transaction = financialTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Financial Transaction with id " + id + " not found"));

        BigDecimal responseAmount = transaction.getAmount();
        if (currency == Currency.USD) {
            responseAmount = responseAmount.divide(currencyService.getUsdBrlExchangeRate(), 2, RoundingMode.HALF_UP);
        }

        return financialTransactionMapper.toGet_FT_ByIdResponseDto(transaction, responseAmount);
    }
}
