package com.gustavomp.devtrackerapi.models;

import com.gustavomp.devtrackerapi.models.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransaction {

    @Id
    private Long id;

    private String description;

    private BigDecimal amount;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne
    private Project project;

}
