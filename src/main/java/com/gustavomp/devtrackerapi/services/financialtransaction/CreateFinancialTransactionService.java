package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFinancialTransactionRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.CreateFinancialTransactionResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateFinancialTransactionService {

    private final ProjectRepository projectRepository;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialTransactionMapper financialTransactionMapper;

    public CreateFinancialTransactionResponseDto execute(CreateFinancialTransactionRequestDto createFinancialTransactionRequestDto) {
        Project project = projectRepository.findById(createFinancialTransactionRequestDto.projectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project with id " + createFinancialTransactionRequestDto.projectId() + " not found"
                ));

        FinancialTransaction financialTransaction = financialTransactionMapper.toEntity(createFinancialTransactionRequestDto);
        financialTransaction.setProject(project);

        FinancialTransaction savedFinancialTransaction = financialTransactionRepository.save(financialTransaction);

        return financialTransactionMapper.toCreateFinancialTransactionResponseDto(savedFinancialTransaction);
    }

}
