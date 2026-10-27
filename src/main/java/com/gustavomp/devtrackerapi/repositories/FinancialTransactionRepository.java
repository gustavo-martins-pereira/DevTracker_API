package com.gustavomp.devtrackerapi.repositories;

import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {

    List<FinancialTransaction> findAllByProjectId(Long projectId);

}
