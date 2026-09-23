package com.gustavomp.devtrackerapi.repositories;

import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {

    List<FinancialTransaction> findAllByProjectId(Long projectId);

}
