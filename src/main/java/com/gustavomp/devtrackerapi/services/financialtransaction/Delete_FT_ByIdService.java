package com.gustavomp.devtrackerapi.services.financialtransaction;

import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.repositories.FinancialTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Delete_FT_ByIdService {

    private final FinancialTransactionRepository financialTransactionRepository;

    public void execute(Long id) {
        Optional<FinancialTransaction> financialTransactionOptional = financialTransactionRepository.findById(id);
        if (financialTransactionOptional.isEmpty()) throw new EntityNotFoundException("Financial Transaction with id " + id + " not found");

        FinancialTransaction financialTransaction = financialTransactionOptional.get();

        financialTransactionRepository.delete(financialTransaction);
    }

}
