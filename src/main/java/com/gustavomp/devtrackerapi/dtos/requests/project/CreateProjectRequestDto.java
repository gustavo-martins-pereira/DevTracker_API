package com.gustavomp.devtrackerapi.dtos.requests.project;

import com.gustavomp.devtrackerapi.annotations.ValidProjectType;
import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@ValidProjectType
public record CreateProjectRequestDto(
        @NotBlank(message = "The 'name' couldn't be blank")
        @Size(min = 3, message = "The 'name' must be greater than 2 characters")
        String name,

        @NotBlank(message = "The 'description' couldn't be blank")
        @Size(min = 5, message = "The 'description' must be greater than 4 characters")
        String description,

        @NotNull(message = "\"The 'workedHours' is required\"")
        @PositiveOrZero(message = "The 'workedHours' must be >= than 0")
        Integer workedHours,

        @NotNull(message = "The 'startDate' is required")
        LocalDate startDate,

        @Future(message = "The 'dueDate' must be in the future")
        LocalDate dueDate,

        @NotNull(message = "The 'projectStatus' is required")
        ProjectStatus projectStatus,

        @NotNull(message = "The 'clientId' is required")
        @Positive(message = "The 'clientId' must be a positive ID")
        Long clientId,

        @NotBlank(message = "The 'projectType' is required. Available Options: [FIXED_PRICE, HOURLY_RATE]")
        String projectType,

        @Positive(message = "The 'budget' must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "The 'budget' format must be invalid (e.g. 1000.00)")
        BigDecimal budget,

        @Positive(message = "The 'hourlyRate' must be greater than 0")
        @Digits(integer = 6, fraction = 2, message = "The 'hourlyRate' format must be invalid (e.g. 50.00)")
        BigDecimal hourlyRate
) {}
