package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAll_FT_ByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.models.enums.Currency;
import com.gustavomp.devtrackerapi.models.enums.TransactionType;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import com.gustavomp.devtrackerapi.services.awesomeapi.CurrencyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get All Financial Transactions by Project Id Service")
public class GetAllFTByProjectIdServiceTest {

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;

    @Mock
    private FinancialTransactionMapper financialTransactionMapper;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private GetAll_FT_ByProjectIdService getAllFtByProjectIdService;

    @Test
    @DisplayName("Get Financial Transactions | Default currency | Returns BRL amount")
    void execute_whenDefaultCurrency_shouldReturnOriginalAmount() {
        // Arrange
        FinancialTransaction transaction = createTransaction(1L, "100.01");
        when(financialTransactionRepository.findAllByProjectId(1L)).thenReturn(List.of(transaction));

        var expectedResponse = createResponse(transaction, new BigDecimal("100.01"));
        when(financialTransactionMapper.toGetAll_FT_ByProjectIdResponseDto(transaction, new BigDecimal("100.01")))
                .thenReturn(expectedResponse);

        // Act
        var actualResponse = getAllFtByProjectIdService.execute(1L);

        // Assert
        assertEquals(List.of(expectedResponse), actualResponse);
        assertEquals(new BigDecimal("100.01"), transaction.getAmount());

        verify(financialTransactionRepository).findAllByProjectId(1L);
        verify(financialTransactionMapper).toGetAll_FT_ByProjectIdResponseDto(transaction, new BigDecimal("100.01"));

        verifyNoInteractions(currencyService);
    }

    @Test
    @DisplayName("Get Financial Transactions | BRL currency | Preserves the amount")
    void execute_whenBrlCurrency_shouldReturnOriginalAmount() {
        // Arrange
        FinancialTransaction transaction = createTransaction(1L, "100.01");
        when(financialTransactionRepository.findAllByProjectId(1L)).thenReturn(List.of(transaction));

        var expectedResponse = createResponse(transaction, new BigDecimal("100.01"));
        when(financialTransactionMapper.toGetAll_FT_ByProjectIdResponseDto(transaction, new BigDecimal("100.01")))
                .thenReturn(expectedResponse);

        // Act
        var actualResponse = getAllFtByProjectIdService.execute(1L, Currency.BRL);

        // Assert
        assertEquals(List.of(expectedResponse), actualResponse);
        assertEquals(new BigDecimal("100.01"), transaction.getAmount());

        verify(financialTransactionRepository).findAllByProjectId(1L);
        verify(financialTransactionMapper).toGetAll_FT_ByProjectIdResponseDto(transaction, new BigDecimal("100.01"));

        verifyNoInteractions(currencyService);
    }

    @Test
    @DisplayName("Get Financial Transactions | USD currency | Converts and rounds the amount")
    void execute_whenUsdCurrency_shouldReturnConvertedAmount() {
        // Arrange
        FinancialTransaction transaction = createTransaction(1L, "100.01");
        when(financialTransactionRepository.findAllByProjectId(1L)).thenReturn(List.of(transaction));

        when(currencyService.getUsdBrlExchangeRate()).thenReturn(new BigDecimal("3.00"));

        var expectedResponse = createResponse(transaction, new BigDecimal("33.34"));
        when(financialTransactionMapper.toGetAll_FT_ByProjectIdResponseDto(transaction, new BigDecimal("33.34")))
                .thenReturn(expectedResponse);

        // Act
        var actualResponse = getAllFtByProjectIdService.execute(1L, Currency.USD);

        // Assert
        assertEquals(List.of(expectedResponse), actualResponse);
        assertEquals(new BigDecimal("100.01"), transaction.getAmount());

        verify(financialTransactionRepository).findAllByProjectId(1L);
        verify(financialTransactionMapper).toGetAll_FT_ByProjectIdResponseDto(transaction, new BigDecimal("33.34"));
        verify(currencyService).getUsdBrlExchangeRate();
    }

    @Test
    @DisplayName("Get All Financial Transactions | Empty BRL result | Returns an empty list")
    void execute_whenNoTransactionsExist_shouldReturnEmptyList() {
        // Arrange
        when(financialTransactionRepository.findAllByProjectId(1L)).thenReturn(List.of());

        // Act
        var actualResponse = getAllFtByProjectIdService.execute(1L);

        // Assert
        assertTrue(actualResponse.isEmpty());

        verify(financialTransactionRepository).findAllByProjectId(1L);

        verifyNoInteractions(financialTransactionMapper, currencyService);
    }

    @Test
    @DisplayName("Get All Financial Transactions | Empty USD result | Returns an empty list")
    void execute_whenNoTransactionsExistInUsd_shouldReturnEmptyList() {
        // Arrange
        when(financialTransactionRepository.findAllByProjectId(1L)).thenReturn(List.of());

        // Act
        var actualResponse = getAllFtByProjectIdService.execute(1L, Currency.USD);

        // Assert
        assertTrue(actualResponse.isEmpty());

        verify(financialTransactionRepository).findAllByProjectId(1L);

        verifyNoInteractions(financialTransactionMapper, currencyService);
    }

    @Test
    @DisplayName("Get All Financial Transactions | Multiple transactions | Preserves result order")
    void execute_whenMultipleTransactionsExist_shouldReturnMappedTransactionsInOrder() {
        // Arrange
        FinancialTransaction first = createTransaction(1L, "100.00");
        FinancialTransaction second = createTransaction(2L, "50.00");
        second.setTransactionType(TransactionType.EXPENSE);
        when(financialTransactionRepository.findAllByProjectId(1L)).thenReturn(List.of(first, second));

        var firstResponse = createResponse(first, first.getAmount());
        when(financialTransactionMapper.toGetAll_FT_ByProjectIdResponseDto(first, first.getAmount())).thenReturn(firstResponse);

        var secondResponse = createResponse(second, second.getAmount());
        when(financialTransactionMapper.toGetAll_FT_ByProjectIdResponseDto(second, second.getAmount())).thenReturn(secondResponse);

        // Act
        var actualResponse = getAllFtByProjectIdService.execute(1L);

        // Assert
        assertEquals(List.of(firstResponse, secondResponse), actualResponse);

        verify(financialTransactionMapper).toGetAll_FT_ByProjectIdResponseDto(first, first.getAmount());
        verify(financialTransactionMapper).toGetAll_FT_ByProjectIdResponseDto(second, second.getAmount());

        verifyNoInteractions(currencyService);
    }

    private FinancialTransaction createTransaction(long id, String amount) {
        return new FinancialTransaction(
                id,
                "Website payment",
                new BigDecimal(amount),
                LocalDate.of(2026, 1, 1),
                TransactionType.INCOME,
                null
        );
    }

    private GetAll_FT_ByProjectIdResponseDto createResponse(FinancialTransaction transaction, BigDecimal amount) {
        return new GetAll_FT_ByProjectIdResponseDto(
                transaction.getId(),
                transaction.getDescription(),
                amount,
                transaction.getDate(),
                transaction.getTransactionType()
        );
    }
}
