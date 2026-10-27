package com.gustavomp.devtrackerapi.dtos.responses.project;

import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;

public record UpdateProjectStatusByIdResponseDto(
        Long id,
        String name,
        ProjectStatus projectStatus,
        Long clientId
) {}
