package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.responses.project.GetAllProjectsResponseDto;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.HourlyRateProject;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get All Projects Service")
public class GetAllProjectsServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private GetAllProjectsService getAllProjectsService;

    @Test
    @DisplayName("Get All Projects | Projects exist | Returns mapped projects")
    void execute_whenProjectsExists_shouldReturnProjects() {
        // Arrange
        Project firstProject = new FixedPriceProject();
        firstProject.setProjectStatus(ProjectStatus.IN_PROGRESS);
        Project secondProject = new HourlyRateProject();
        secondProject.setProjectStatus(ProjectStatus.COMPLETED);
        when(projectRepository.findAll()).thenReturn(List.of(firstProject, secondProject));

        GetAllProjectsResponseDto firstResponse = fixedPriceResponse(1L, ProjectStatus.IN_PROGRESS);
        GetAllProjectsResponseDto secondResponse = hourlyRateResponse(2L, ProjectStatus.COMPLETED);
        when(projectMapper.toAllProjectsResponseDto(firstProject)).thenReturn(firstResponse);
        when(projectMapper.toAllProjectsResponseDto(secondProject)).thenReturn(secondResponse);

        // Act
        var actualResponse = getAllProjectsService.execute();

        // Assert
        assertEquals(List.of(firstResponse, secondResponse), actualResponse);

        verify(projectRepository).findAll();
        verify(projectMapper).toAllProjectsResponseDto(firstProject);
        verify(projectMapper).toAllProjectsResponseDto(secondProject);
    }

    @Test
    @DisplayName("Get All Projects | No projects exist | Returns an empty list")
    void execute_whenProjectsNotExists_shouldReturnEmptyList() {
        // Arrange
        when(projectRepository.findAll()).thenReturn(List.of());

        // Act
        var actualResponse = getAllProjectsService.execute();

        // Assert
        assertTrue(actualResponse.isEmpty());

        verify(projectRepository).findAll();
        verifyNoInteractions(projectMapper);
    }

    @Test
    @DisplayName("Get All Projects | Matching status | Returns mapped projects")
    void execute_whenProjectsWithStatusExists_shouldReturnProjects() {
        // Arrange
        Project firstProject = new FixedPriceProject();
        firstProject.setProjectStatus(ProjectStatus.COMPLETED);
        Project secondProject = new HourlyRateProject();
        secondProject.setProjectStatus(ProjectStatus.COMPLETED);
        when(projectRepository.findAllByProjectStatus(ProjectStatus.COMPLETED)).thenReturn(List.of(firstProject, secondProject));

        GetAllProjectsResponseDto firstResponse = fixedPriceResponse(1L, ProjectStatus.COMPLETED);
        GetAllProjectsResponseDto secondResponse = hourlyRateResponse(2L, ProjectStatus.COMPLETED);
        when(projectMapper.toAllProjectsResponseDto(firstProject)).thenReturn(firstResponse);
        when(projectMapper.toAllProjectsResponseDto(secondProject)).thenReturn(secondResponse);

        // Act
        var actualResponse = getAllProjectsService.execute(ProjectStatus.COMPLETED);

        // Assert
        assertEquals(List.of(firstResponse, secondResponse), actualResponse);

        verify(projectRepository).findAllByProjectStatus(ProjectStatus.COMPLETED);
        verify(projectMapper).toAllProjectsResponseDto(firstProject);
        verify(projectMapper).toAllProjectsResponseDto(secondProject);
    }

    @Test
    @DisplayName("Get All Projects | No projects with matching status | Returns an empty list")
    void execute_whenProjectsWithStatusNotExists_shouldReturnEmptyList() {
        // Arrange
        when(projectRepository.findAllByProjectStatus(ProjectStatus.COMPLETED)).thenReturn(List.of());

        // Act
        var actualResponse = getAllProjectsService.execute(ProjectStatus.COMPLETED);

        // Assert
        assertTrue(actualResponse.isEmpty());

        verify(projectRepository).findAllByProjectStatus(ProjectStatus.COMPLETED);
        verifyNoInteractions(projectMapper);
    }

    private GetAllProjectsResponseDto fixedPriceResponse(long id, ProjectStatus status) {
        return new GetAllProjectsResponseDto(
                id, "Website",
                "Client website",
                0,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                status,
                1L,
                "FIXED_PRICE",
                new BigDecimal("1000.00"),
                null
        );
    }

    private GetAllProjectsResponseDto hourlyRateResponse(long id, ProjectStatus status) {
        return new GetAllProjectsResponseDto(
                id,
                "API",
                "Client API",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                status,
                1L,
                "HOURLY_RATE",
                null,
                new BigDecimal("50.00")
        );
    }

}

