package com.gustavomp.devtrackerapi.dtos.requests.project;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProjectByIdRequestDto(
        @Size(min = 3, message = "The 'name' must be greater than 2 characters")
        String name,

        @Size(min = 5, message = "The 'description' must be greater than 4 characters")
        String description,

        @PositiveOrZero(message = "The 'workedHours' must be >= than 0")
        Integer workedHours,

        @PastOrPresent
        LocalDate startDate,

        LocalDate dueDate
) {}
