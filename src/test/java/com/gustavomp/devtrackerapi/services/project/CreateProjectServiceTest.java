package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.CreateProjectResponseDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("Create Project Service")
public class CreateProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private CreateProjectService createProjectService;

    CreateProjectRequestDto request;

    @BeforeEach
    void setup() {
        request = new CreateProjectRequestDto(
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
    }

    @Test
    @DisplayName("Create Project | Existing client and no matching project | Saves and returns the saved project")
    void execute_whenNoMatchingProjectExists_shouldReturnCreatedProject() {
        // Arrange
        Client client = new Client();
        when(clientRepository.findById(request.clientId())).thenReturn(Optional.of(client));

        when(projectRepository.findByName(request.name())).thenReturn(Optional.empty());

        Project projectToSave = new FixedPriceProject();
        when(projectMapper.toEntity(request)).thenReturn(projectToSave);

        Project savedProject = new FixedPriceProject();
        savedProject.setId(1L);
        when(projectRepository.save(projectToSave)).thenReturn(savedProject);

        CreateProjectResponseDto expectedResponse = new CreateProjectResponseDto(
                savedProject.getId(),
                request.name(),
                request.description(),
                request.workedHours(),
                request.startDate(),
                request.dueDate(),
                request.projectStatus(),
                request.clientId(),
                request.projectType(),
                request.budget(),
                request.hourlyRate()
        );
        when(projectMapper.toCreateProjectResponseDto(savedProject)).thenReturn(expectedResponse);

        // Act
        var actualResponse = createProjectService.execute(request);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        assertSame(client, projectToSave.getClient());

        verify(clientRepository).findById(request.clientId());
        verify(projectRepository).findByName(request.name());

        verify(projectMapper).toEntity(request);
        verify(projectRepository).save(projectToSave);
        verify(projectMapper).toCreateProjectResponseDto(savedProject);
    }

    @Test
    @DisplayName("Create Project | Client not found | Throws EntityNotFoundException")
    void execute_whenClientIsNotFounded_shouldThrowEntityNotFoundException() {
        // Arrange
        when(clientRepository.findById(request.clientId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> createProjectService.execute(request));

        verify(clientRepository).findById(request.clientId());
        verifyNoInteractions(projectRepository, projectMapper);
    }

    @Test
    @DisplayName("Create Project | Matching project exists | Throws EntityNotFoundException")
    void execute_whenMatchingProjectExists_shouldThrowEntityNotFoundException() {
        // Arrange
        when(clientRepository.findById(request.clientId())).thenReturn(Optional.of(new Client()));
        when(projectRepository.findByName(request.name())).thenReturn(Optional.of(new FixedPriceProject()));

        // Act & Assert
        assertThrows(EntityAlreadyExistsException.class, () -> createProjectService.execute(request));

        verify(projectRepository).findByName(request.name());
        verify(projectRepository, never()).save(any());
        verifyNoInteractions(projectMapper);
    }
}
