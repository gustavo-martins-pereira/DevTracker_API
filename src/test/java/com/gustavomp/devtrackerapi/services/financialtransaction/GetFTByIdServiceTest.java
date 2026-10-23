package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Get_FT_ByIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.models.enums.Currency;
import com.gustavomp.devtrackerapi.models.enums.TransactionType;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import com.gustavomp.devtrackerapi.services.awesomeapi.CurrencyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Financial Transaction by Id Service")
public class GetFTByIdServiceTest {

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;
    @Mock
    private FinancialTransactionMapper financialTransactionMapper;
    @Mock
    private CurrencyService currencyService;
    @InjectMocks
    private Get_FT_ByIdService service;

    @Test
    @DisplayName("Get Financial Transaction | Default currency | Returns BRL amount")
    void execute_whenDefaultCurrency_shouldReturnOriginalAmount() {
        // Arrange
        FinancialTransaction transaction = transaction();
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        var expectedResponse = response(transaction, new BigDecimal("100.01"));
        when(financialTransactionMapper.toGet_FT_ByIdResponseDto(transaction, new BigDecimal("100.01")))
                .thenReturn(expectedResponse);

        // Act
        var actualResponse = service.execute(1L);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        assertEquals(new BigDecimal("100.01"), transaction.getAmount());

        verify(financialTransactionRepository).findById(1L);
        verify(financialTransactionMapper).toGet_FT_ByIdResponseDto(transaction, new BigDecimal("100.01"));

        verifyNoInteractions(currencyService);
    }

    @Test
    @DisplayName("Get Financial Transaction | BRL currency | Preserves the amount")
    void execute_whenBrlCurrency_shouldReturnOriginalAmount() {
        // Arrange
        FinancialTransaction transaction = transaction();
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        var expectedResponse = response(transaction, new BigDecimal("100.01"));
        when(financialTransactionMapper.toGet_FT_ByIdResponseDto(transaction, new BigDecimal("100.01")))
                .thenReturn(expectedResponse);

        // Act
        var actualResponse = service.execute(1L, Currency.BRL);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        assertEquals(new BigDecimal("100.01"), transaction.getAmount());

        verify(financialTransactionRepository).findById(1L);
        verify(financialTransactionMapper).toGet_FT_ByIdResponseDto(transaction, new BigDecimal("100.01"));

        verifyNoInteractions(currencyService);
    }

    @Test
    @DisplayName("Get Financial Transaction | USD currency | Converts and rounds the amount")
    void execute_whenUsdCurrency_shouldReturnConvertedAmount() {
        // Arrange
        FinancialTransaction transaction = transaction();
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        var expectedResponse = response(transaction, new BigDecimal("33.34"));
        when(currencyService.getUsdBrlExchangeRate()).thenReturn(new BigDecimal("3.00"));

        when(financialTransactionMapper.toGet_FT_ByIdResponseDto(transaction, new BigDecimal("33.34")))
                .thenReturn(expectedResponse);

        // Act
        var actualResponse = service.execute(1L, Currency.USD);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        assertEquals(new BigDecimal("100.01"), transaction.getAmount());

        verify(financialTransactionRepository).findById(1L);
        verify(financialTransactionMapper).toGet_FT_ByIdResponseDto(transaction, new BigDecimal("33.34"));
        verify(currencyService).getUsdBrlExchangeRate();
    }

    @Test
    @DisplayName("Get Financial Transaction | Transaction not found | Throws EntityNotFoundException")
    void execute_whenTransactionDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> service.execute(1L, Currency.USD));

        verify(financialTransactionRepository).findById(1L);

        verifyNoInteractions(financialTransactionMapper, currencyService);
    }

    private FinancialTransaction transaction() {
        return new FinancialTransaction(1L, "Website payment", new BigDecimal("100.01"),
                LocalDate.of(2026, 1, 1), TransactionType.INCOME, null);
    }

    private Get_FT_ByIdResponseDto response(FinancialTransaction transaction, BigDecimal amount) {
        return new Get_FT_ByIdResponseDto(transaction.getId(), transaction.getDescription(), amount,
                transaction.getDate(), transaction.getTransactionType(), 1L, 1L, "FIXED_PRICE");
    }
}
