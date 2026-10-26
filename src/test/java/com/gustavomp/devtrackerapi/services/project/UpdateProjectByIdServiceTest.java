package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.UpdateProjectByIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Project by Id Service")
public class UpdateProjectByIdServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private UpdateProjectByIdService updateProjectByIdService;

    @Test
    @DisplayName("Update Project | Valid dates | Saves and returns the updated project")
    void execute_whenProjectExists_shouldReturnUpdatedProject() {
        // Arrange
        Project oldProject = new FixedPriceProject();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(oldProject));

        UpdateProjectByIdRequestDto request = new UpdateProjectByIdRequestDto(
                "Website",
                "Updated website",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
        Project projectToSave = new FixedPriceProject();
        when(projectMapper.toEntity(request, oldProject)).thenReturn(projectToSave);

        Project savedProject = new FixedPriceProject();
        when(projectRepository.save(projectToSave)).thenReturn(savedProject);

        UpdateProjectByIdResponseDto expectedResponse = new UpdateProjectByIdResponseDto(
                1L,
                request.name(),
                request.description(),
                request.workedHours(),
                request.startDate(),
                request.dueDate(),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("1000.00"),
                null
        );
        when(projectMapper.toUpdateProjectByIdResponseDto(savedProject)).thenReturn(expectedResponse);

        // Act
        var actualResponse = updateProjectByIdService.execute(1L, request);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(projectRepository).findById(1L);
        verify(projectMapper).toEntity(request, oldProject);
        verify(projectRepository).save(projectToSave);
        verify(projectMapper).toUpdateProjectByIdResponseDto(savedProject);
    }

    @Test
    @DisplayName("Update Project | Equal start and due dates | Saves and returns the updated project")
    void execute_whenDatesAreEqual_shouldReturnUpdatedProject() {
        // Arrange
        Project oldProject = new FixedPriceProject();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(oldProject));

        Project projectToSave = new FixedPriceProject();
        UpdateProjectByIdRequestDto request = new UpdateProjectByIdRequestDto(
                "Website",
                "Updated website",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 1)
        );
        when(projectMapper.toEntity(request, oldProject)).thenReturn(projectToSave);

        Project savedProject = new FixedPriceProject();
        when(projectRepository.save(projectToSave)).thenReturn(savedProject);

        UpdateProjectByIdResponseDto expectedResponse = new UpdateProjectByIdResponseDto(
                1L,
                request.name(),
                request.description(),
                request.workedHours(),
                request.startDate(),
                request.dueDate(),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("1000.00"),
                null
        );
        when(projectMapper.toUpdateProjectByIdResponseDto(savedProject)).thenReturn(expectedResponse);

        // Act
        var actualResponse = updateProjectByIdService.execute(1L, request);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(projectRepository).findById(1L);
        verify(projectMapper).toEntity(request, oldProject);
        verify(projectRepository).save(projectToSave);
        verify(projectMapper).toUpdateProjectByIdResponseDto(savedProject);
    }

    @Test
    @DisplayName("Update Project | Project not found | Throws EntityNotFoundException")
    void execute_whenProjectDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        UpdateProjectByIdRequestDto request = new UpdateProjectByIdRequestDto(
                "Website",
                "Updated website",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> updateProjectByIdService.execute(1L, request));

        verify(projectRepository).findById(1L);
        verify(projectRepository, never()).save(any());
        verifyNoInteractions(projectMapper);
    }

    @Test
    @DisplayName("Update Project | Start date after due date | Throws IllegalArgumentException")
    void execute_whenStartDateIsAfterDueDate_shouldThrowIllegalArgumentException() {
        // Arrange
        UpdateProjectByIdRequestDto request = new UpdateProjectByIdRequestDto(
                "Website",
                "Updated website",
                10,
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 1, 1)
        );
        when(projectRepository.findById(1L)).thenReturn(Optional.of(new FixedPriceProject()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> updateProjectByIdService.execute(1L, request));

        verify(projectRepository).findById(1L);
        verify(projectRepository, never()).save(any());
        verifyNoInteractions(projectMapper);
    }
}

