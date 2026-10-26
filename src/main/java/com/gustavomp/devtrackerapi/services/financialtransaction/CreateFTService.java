package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFTRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.CreateFTResponseDto;
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
public class CreateFTService {

    private final ProjectRepository projectRepository;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialTransactionMapper financialTransactionMapper;

    public CreateFTResponseDto execute(CreateFTRequestDto createFTRequestDto) {
        Project project = projectRepository.findById(createFTRequestDto.projectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project with id " + createFTRequestDto.projectId() + " not found"
                ));

        FinancialTransaction financialTransaction = financialTransactionMapper.toEntity(createFTRequestDto);
        financialTransaction.setProject(project);

        FinancialTransaction savedFinancialTransaction = financialTransactionRepository.save(financialTransaction);

        return financialTransactionMapper.toCreateFTResponseDto(savedFinancialTransaction);
    }

}
