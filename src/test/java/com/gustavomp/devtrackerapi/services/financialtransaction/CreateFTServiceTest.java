package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFTRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.CreateFTResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.enums.TransactionType;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
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
@DisplayName("Create Financial Transaction Service")
public class CreateFTServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;

    @Mock
    private FinancialTransactionMapper financialTransactionMapper;

    @InjectMocks
    private CreateFTService createFTService;

    private final CreateFTRequestDto request = new CreateFTRequestDto(
            "Website payment",
            new BigDecimal("1000.00"),
            LocalDate.of(2026, 1, 1),
            TransactionType.INCOME,
            1L
    );

    @Test
    @DisplayName("Create Financial Transaction | Existing project | Saves and returns the transaction")
    void execute_whenProjectExists_shouldReturnCreatedTransaction() {
        // Arrange
        Project project = new FixedPriceProject();
        when(projectRepository.findById(request.projectId())).thenReturn(Optional.of(project));

        FinancialTransaction transactionToSave = new FinancialTransaction();
        when(financialTransactionMapper.toEntity(request)).thenReturn(transactionToSave);

        FinancialTransaction savedTransaction = new FinancialTransaction();
        savedTransaction.setId(1L);
        when(financialTransactionRepository.save(transactionToSave)).thenReturn(savedTransaction);

        var expectedResponse = new CreateFTResponseDto(
                1L,
                request.description(),
                request.amount(),
                request.date(),
                request.transactionType(),
                request.projectId()
        );
        when(financialTransactionMapper.toCreateFTResponseDto(savedTransaction)).thenReturn(expectedResponse);

        // Act
        var actualResponse = createFTService.execute(request);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(projectRepository).findById(request.projectId());
        verify(financialTransactionRepository).save(transactionToSave);
        verify(financialTransactionMapper).toEntity(request);
        verify(financialTransactionMapper).toCreateFTResponseDto(savedTransaction);
    }

    @Test
    @DisplayName("Create Financial Transaction | Project not found | Throws EntityNotFoundException")
    void execute_whenProjectDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        when(projectRepository.findById(request.projectId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> createFTService.execute(request));

        verifyNoInteractions(financialTransactionRepository);
        verifyNoInteractions(financialTransactionMapper);
    }
}
