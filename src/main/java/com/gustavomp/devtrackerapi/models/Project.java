package com.gustavomp.devtrackerapi.models;

import com.gustavomp.devtrackerapi.models.enums.BillingType;
import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
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
public class Project {

    @Id
    private Long id;

    private String name;

    private String description;

    private BigDecimal budget;

    private BigDecimal hourlyRate;

    private Integer workedHours;

    private LocalDate startDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

    @Enumerated(EnumType.STRING)
    private BillingType billingType;

    @ManyToOne
    private Client client;

}
