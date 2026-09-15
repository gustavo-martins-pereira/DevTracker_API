package com.gustavomp.devtrackerapi.repositories;

import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {}
