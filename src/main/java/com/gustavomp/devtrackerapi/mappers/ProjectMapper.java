package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.CreateProjectResponseDto;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.HourlyRateProject;
import com.gustavomp.devtrackerapi.models.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    // Create Project
    // Request
    default Project toEntity(CreateProjectRequestDto dto) {
        if ("FIXED_PRICE".equalsIgnoreCase(dto.projectType())) {
            return toFixedPriceProject(dto);
        }

        if ("HOURLY_RATE".equalsIgnoreCase(dto.projectType())) {
            return toHourlyRateProject(dto);
        }

        throw new IllegalArgumentException("Invalid project type: " + dto.projectType());
    }

    FixedPriceProject toFixedPriceProject(CreateProjectRequestDto dto);
    HourlyRateProject toHourlyRateProject(CreateProjectRequestDto dto);

    // Response
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "FIXED_PRICE")
    CreateProjectResponseDto toCreateProjectResponseDto(FixedPriceProject project);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "HOURLY_RATE")
    CreateProjectResponseDto toCreateProjectResponseDto(HourlyRateProject project);

    default CreateProjectResponseDto toCreateProjectResponseDto(Project project) {
        if (project instanceof FixedPriceProject fixedPriceProject) {
            return toCreateProjectResponseDto(fixedPriceProject);
        }

        if (project instanceof HourlyRateProject hourlyRateProject) {
            return toCreateProjectResponseDto(hourlyRateProject);
        }

        throw new IllegalArgumentException("Unsupported project type: " + project.getClass().getSimpleName());
    }

}
