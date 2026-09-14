package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.CreateProjectResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.GetAllProjectsResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.GetProjectByIdResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.UpdateProjectByIdResponseDto;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.HourlyRateProject;
import com.gustavomp.devtrackerapi.models.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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

    // Get All Projects
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "FIXED_PRICE")
    GetAllProjectsResponseDto toAllProjectsResponseDto(FixedPriceProject project);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "HOURLY_RATE")
    GetAllProjectsResponseDto toAllProjectsResponseDto(HourlyRateProject project);

    default GetAllProjectsResponseDto toAllProjectsResponseDto(Project project) {
        if (project instanceof FixedPriceProject fixedPriceProject) {
            return toAllProjectsResponseDto(fixedPriceProject);
        }

        if (project instanceof HourlyRateProject hourlyRateProject) {
            return toAllProjectsResponseDto(hourlyRateProject);
        }

        throw new IllegalArgumentException("Unsupported project type: " + project.getClass().getSimpleName());
    }

    // Get Project by ID
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "FIXED_PRICE")
    GetProjectByIdResponseDto toGetProjectByIdResponseDto(FixedPriceProject project);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "HOURLY_RATE")
    GetProjectByIdResponseDto toGetProjectByIdResponseDto(HourlyRateProject project);

    default GetProjectByIdResponseDto toGetProjectByIdResponseDto(Project project) {
        if (project instanceof FixedPriceProject fixedPriceProject) {
            return toGetProjectByIdResponseDto(fixedPriceProject);
        }

        if (project instanceof HourlyRateProject hourlyRateProject) {
            return toGetProjectByIdResponseDto(hourlyRateProject);
        }

        throw new IllegalArgumentException("Unsupported project type: " + project.getClass().getSimpleName());
    }

    // Update Project by ID
    // Request
    Project toEntity(UpdateProjectByIdRequestDto dto, @MappingTarget Project project);

    // Response
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "FIXED_PRICE")
    UpdateProjectByIdResponseDto toUpdateProjectByIdResponseDto(FixedPriceProject project);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "projectType", constant = "HOURLY_RATE")
    UpdateProjectByIdResponseDto toUpdateProjectByIdResponseDto(HourlyRateProject project);

    default UpdateProjectByIdResponseDto toUpdateProjectByIdResponseDto(Project project) {
        if (project instanceof FixedPriceProject fixedPriceProject) {
            return toUpdateProjectByIdResponseDto(fixedPriceProject);
        }

        if (project instanceof HourlyRateProject hourlyRateProject) {
            return toUpdateProjectByIdResponseDto(hourlyRateProject);
        }

        throw new IllegalArgumentException("Unsupported project type: " + project.getClass().getSimpleName());
    }

}
