package com.gustavomp.devtrackerapi.dtos.responses.project;

import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetProjectByIdResponseDto(
        Long id,
        String name,
        String description,
        Integer workedHours,
        LocalDate startDate,
        LocalDate dueDate,
        ProjectStatus projectStatus,
        Long clientId,
        String projectType,
        BigDecimal budget,
        BigDecimal hourlyRate
) {}
