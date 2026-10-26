package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAllFTByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.models.enums.Currency;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import com.gustavomp.devtrackerapi.services.awesomeapi.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllFTByProjectIdService {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialTransactionMapper financialTransactionMapper;
    private final CurrencyService currencyService;

    public List<GetAllFTByProjectIdResponseDto> execute(Long projectId) {
        return execute(projectId, Currency.BRL);
    }

    public List<GetAllFTByProjectIdResponseDto> execute(Long projectId, Currency currency) {
        List<FinancialTransaction> transactions = financialTransactionRepository.findAllByProjectId(projectId);

        return transactions.stream()
                .map(transaction -> {
                    BigDecimal responseAmount = transaction.getAmount();
                    if (currency == Currency.USD) {
                        responseAmount = responseAmount.divide(currencyService.getUsdBrlExchangeRate(), 2, RoundingMode.HALF_UP);
                    }

                    return financialTransactionMapper.toGetAllFTByProjectIdResponseDto(transaction, responseAmount);
                })
                .toList();
    }
}
