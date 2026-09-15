package com.gustavomp.devtrackerapi.dtos.requests.project;

import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateProjectStatusByIdRequestDto(
        @NotNull
        ProjectStatus projectStatus
) {}
