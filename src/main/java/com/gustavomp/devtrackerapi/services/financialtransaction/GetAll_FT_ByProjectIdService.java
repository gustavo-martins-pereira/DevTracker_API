package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAll_FT_ByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAll_FT_ByProjectIdService {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialTransactionMapper financialTransactionMapper;

    public List<GetAll_FT_ByProjectIdResponseDto> execute(Long projectId) {
        return financialTransactionRepository.findAllByProjectId(projectId)
                .stream()
                .map(financialTransactionMapper::toGetAll_FT_ByProjectIdResponseDto)
                .toList();
    }

}
