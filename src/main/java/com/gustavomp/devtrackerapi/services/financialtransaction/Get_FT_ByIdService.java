package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Get_FT_ByIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.FinancialTransactionMapper;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Get_FT_ByIdService {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialTransactionMapper financialTransactionMapper;

    public Get_FT_ByIdResponseDto execute(Long id) {
        return financialTransactionRepository.findById(id)
                .map(financialTransactionMapper::toGet_FT_ByIdResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Financial Transaction with id " + id + " not found"));
    }

}
