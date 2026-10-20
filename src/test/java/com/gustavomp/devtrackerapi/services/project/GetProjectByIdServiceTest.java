package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.responses.project.GetProjectByIdResponseDto;
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
@DisplayName("Get Project by Id Service")
public class GetProjectByIdServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private GetProjectByIdService getProjectByIdService;

    @Test
    @DisplayName("Get Project by Id | Existing project | Returns the project")
    void execute_whenProjectIsFounded_shouldReturnProject() {
        // Arrange
        Project project = new FixedPriceProject();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        GetProjectByIdResponseDto expectedResponse = new GetProjectByIdResponseDto(
                1L,
                "Website",
                "Client website",
                0,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("1000.00"),
                null
        );
        when(projectMapper.toGetProjectByIdResponseDto(project)).thenReturn(expectedResponse);

        // Act
        var actualResponse = getProjectByIdService.execute(1L);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        verify(projectRepository).findById(1L);
        verify(projectMapper).toGetProjectByIdResponseDto(project);
    }

    @Test
    @DisplayName("Get Project by Id | Project not found | Throws EntityNotFoundException")
    void execute_whenProjectIsNotFounded_shouldThrowEntityNotFoundException() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> getProjectByIdService.execute(1L));

        verify(projectRepository).findById(1L);
        verifyNoInteractions(projectMapper);
    }
}

