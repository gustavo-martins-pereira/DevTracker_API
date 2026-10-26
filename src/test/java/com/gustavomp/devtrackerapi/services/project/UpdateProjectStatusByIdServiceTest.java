package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectStatusByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.UpdateProjectStatusByIdResponseDto;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Update Project Status by Id Service")
public class UpdateProjectStatusByIdServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private UpdateProjectStatusByIdService updateProjectStatusByIdService;

    @Test
    @DisplayName("Update Project Status | Existing project | Saves and returns the updated project")
    void execute_whenProjectExists_shouldReturnUpdatedProject() {
        // Arrange
        Project oldProject = new FixedPriceProject();
        oldProject.setProjectStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(oldProject));

        UpdateProjectStatusByIdRequestDto request = new UpdateProjectStatusByIdRequestDto(ProjectStatus.COMPLETED);
        Project projectToSave = new FixedPriceProject();
        projectToSave.setProjectStatus(ProjectStatus.COMPLETED);
        when(projectMapper.toEntity(request, oldProject)).thenReturn(projectToSave);

        Project savedProject = new FixedPriceProject();
        when(projectRepository.save(projectToSave)).thenReturn(savedProject);

        UpdateProjectStatusByIdResponseDto expectedResponse = new UpdateProjectStatusByIdResponseDto(
                1L,
                "Website",
                ProjectStatus.COMPLETED,
                1L
        );
        when(projectMapper.toUpdateProjectStatusByIdResponseDto(savedProject)).thenReturn(expectedResponse);

        // Act
        var actualResponse = updateProjectStatusByIdService.execute(1L, request);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(projectRepository).findById(1L);
        verify(projectMapper).toEntity(request, oldProject);
        verify(projectRepository).save(projectToSave);
        verify(projectMapper).toUpdateProjectStatusByIdResponseDto(savedProject);
    }

    @Test
    @DisplayName("Update Project Status | Project not found | Throws EntityNotFoundException")
    void execute_whenProjectDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        UpdateProjectStatusByIdRequestDto request = new UpdateProjectStatusByIdRequestDto(ProjectStatus.CANCELLED);
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> updateProjectStatusByIdService.execute(1L, request));

        verify(projectRepository).findById(1L);
        verify(projectRepository, never()).save(any());
        verifyNoInteractions(projectMapper);
    }
}

