package com.gustavomp.devtrackerapi.services.financialtransaction;


import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Delete Financial Transaction by Id Service")
public class DeleteFTByIdServiceTest {

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;
    @InjectMocks
    private DeleteFTByIdService deleteFTByIdService;

    @Test
    @DisplayName("Delete Financial Transaction | Existing transaction | Deletes the transaction")
    void execute_whenTransactionExists_shouldDeleteTransaction() {
        // Arrange
        FinancialTransaction transaction = new FinancialTransaction();
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act
        deleteFTByIdService.execute(1L);

        // Assert
        verify(financialTransactionRepository).findById(1L);
        verify(financialTransactionRepository).delete(transaction);
    }

    @Test
    @DisplayName("Delete Financial Transaction | Transaction not found | Throws EntityNotFoundException")
    void execute_whenTransactionDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        when(financialTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> deleteFTByIdService.execute(1L));

        verify(financialTransactionRepository).findById(1L);
        verify(financialTransactionRepository, never()).delete(any());
    }
}
