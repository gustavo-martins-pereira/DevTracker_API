package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectStatusByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.*;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.exceptions.GlobalExceptionHandler;
import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
import com.gustavomp.devtrackerapi.services.project.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Project Controller")
public class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private CreateProjectService createProjectService;

    @Mock
    private GetAllProjectsService getAllProjectsService;

    @Mock
    private GetProjectByIdService getProjectByIdService;

    @Mock
    private UpdateProjectByIdService updateProjectByIdService;

    @Mock
    private UpdateProjectStatusByIdService updateProjectStatusByIdService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /* ---------- Create Project ---------- */
    private static final String CREATE_FIXED_PRICE_PROJECT_JSON = """
            {
                "name": "Example",
                "description": "Description",
                "workedHours": 10,
                "startDate": "2026-01-01",
                "dueDate": "2026-01-30",
                "projectStatus": "IN_PROGRESS",
                "clientId": 1,
                "projectType": "FIXED_PRICE",
                "budget": 100
            }
            """;

    private static final String CREATE_HOURLY_RATE_PROJECT_JSON = """
            {
                "name": "Example",
                "description": "Description",
                "workedHours": 10,
                "startDate": "2026-01-01",
                "dueDate": "2026-01-30",
                "projectStatus": "IN_PROGRESS",
                "clientId": 1,
                "projectType": "HOURLY_RATE",
                "hourlyRate": 100
            }
            """;

    @Test
    @DisplayName("Create FixedPriceProject | Valid request | Returns 201 and created project")
    void createFixedPriceProject_whenValidRequest_shouldReturnCreatedProject() throws Exception {
        // Arrange
        var request = new CreateProjectRequestDto(
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("100"),
                null
        );
        var response = new CreateProjectResponseDto(
                1L,
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("100"),
                null
        );
        when(createProjectService.execute(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content(CREATE_FIXED_PRICE_PROJECT_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                        "id": 1,
                        "name": "Example",
                        "description": "Description",
                        "workedHours": 10,
                        "startDate": "2026-01-01",
                        "dueDate": "2026-01-30",
                        "projectStatus": "IN_PROGRESS",
                        "clientId": 1,
                        "projectType": "FIXED_PRICE",
                        "budget": 100
                        }
                        """));

        verify(createProjectService).execute(request);
    }

    @Test
    @DisplayName("Create HourlyRateProject | Valid request | Returns 201 and created project")
    void createHourlyRateProject_whenValidRequest_shouldReturnCreatedProject() throws Exception {
        // Arrange
        var request = new CreateProjectRequestDto(
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "HOURLY_RATE",
                null,
                new BigDecimal("100")
        );
        var response = new CreateProjectResponseDto(
                1L,
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "HOURLY_RATE",
                null,
                new BigDecimal("100")
        );
        when(createProjectService.execute(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content(CREATE_HOURLY_RATE_PROJECT_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                        "id": 1,
                        "name": "Example",
                        "description": "Description",
                        "workedHours": 10,
                        "startDate": "2026-01-01",
                        "dueDate": "2026-01-30",
                        "projectStatus": "IN_PROGRESS",
                        "clientId": 1,
                        "projectType": "HOURLY_RATE",
                        "hourlyRate": 100
                        }
                        """));

        verify(createProjectService).execute(request);
    }

    @Test
    @DisplayName("Create Project | Client not found | Throws EntityNotFoundException and 404")
    void createProject_whenClientNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        var request = new CreateProjectRequestDto(
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("100"),
                null
        );
        when(createProjectService.execute(request)).thenThrow(new EntityNotFoundException("Client not found"));

        // Act & Assert
        mockMvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content(CREATE_FIXED_PRICE_PROJECT_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Client not found"));

        verify(createProjectService).execute(request);
    }

    @Test
    @DisplayName("Create Project | Existing project with same name | Throws EntityAlreadyExistsException and 409")
    void createProject_whenProjectWithSameNameAlreadyExists_shouldReturnConflict() throws Exception {
        // Arrange
        var request = new CreateProjectRequestDto(
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("100"),
                null
        );
        when(createProjectService.execute(request)).thenThrow(new EntityAlreadyExistsException("Project with this name already exists"));

        // Act & Assert
        mockMvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content(CREATE_FIXED_PRICE_PROJECT_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Project with this name already exists"));

        verify(createProjectService).execute(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"\"}",
            "{\"name\":\"An\"}",
            "{\"description\":\"\"}",
            "{\"description\":\"Abed\"}",
            "{\"workedHours\":-1}",
            "{\"startDate\":\"\"}",
            "{\"startDate\":\"1\"}",
            "{\"dueDate\":\"\"}",
            "{\"dueDate\":\"1\"}",
            "{\"projectStatus\":\"\"}",
            "{\"projectStatus\":\"NA\"}",
            "{\"clientId\":\"\"}",
            "{\"clientId\":-1}",
            "{\"projectType\":\"\"}",
            "{\"projectType\":\"NA\"}",
            "{\"budget\":\"\"}",
            "{\"budget\":-1}",
    })
    @DisplayName("Create FixedPriceProject | Invalid Request params | Throws BadRequestException and 400")
    void createFixedPriceProject_whenInvalidRequest_shouldReturnBadRequest(String invalidField) throws Exception {
        // Arrange
        String field = invalidField.split(":")[0];
        field = field.replace("{", "").replace("\"", "");

        String replacement = invalidField.substring(1, invalidField.length() - 1);

        String fieldPattern = "\"" + field + "\":\\s*(\"[^\"]*\"|-?\\d+(\\.\\d+)?|null)";
        String invalidJson = CREATE_FIXED_PRICE_PROJECT_JSON.replaceFirst(
                fieldPattern,
                Matcher.quoteReplacement(replacement)
        );
        assertNotEquals(CREATE_FIXED_PRICE_PROJECT_JSON, invalidJson, "The test must replace the selected field");

        // Act & Assert
        mockMvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error." + field).isNotEmpty());

        verifyNoInteractions(createProjectService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"\"}",
            "{\"name\":\"An\"}",
            "{\"description\":\"\"}",
            "{\"description\":\"Abed\"}",
            "{\"workedHours\":-1}",
            "{\"startDate\":\"\"}",
            "{\"startDate\":\"1\"}",
            "{\"dueDate\":\"\"}",
            "{\"dueDate\":\"1\"}",
            "{\"projectStatus\":\"\"}",
            "{\"projectStatus\":\"NA\"}",
            "{\"clientId\":\"\"}",
            "{\"clientId\":-1}",
            "{\"projectType\":\"\"}",
            "{\"projectType\":\"NA\"}",
            "{\"hourlyRate\":\"\"}",
            "{\"hourlyRate\":\"-1\"}",
    })
    @DisplayName("Create HourlyRateProject | Invalid Request params | Throws BadRequestException and 400")
    void createHourlyRateProject_whenInvalidRequest_shouldReturnBadRequest(String invalidField) throws Exception {
        // Arrange
        String field = invalidField.split(":")[0];
        field = field.replace("{", "").replace("\"", "");

        String replacement = invalidField.substring(1, invalidField.length() - 1);

        String fieldPattern = "\"" + field + "\":\\s*(\"[^\"]*\"|-?\\d+(\\.\\d+)?|null)";
        String invalidJson = CREATE_HOURLY_RATE_PROJECT_JSON.replaceFirst(
                fieldPattern,
                Matcher.quoteReplacement(replacement)
        );
        assertNotEquals(CREATE_HOURLY_RATE_PROJECT_JSON, invalidJson, "The test must replace the selected field");

        // Act & Assert
        mockMvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error." + field).isNotEmpty());

        verifyNoInteractions(createProjectService);
    }

    @Test
    @DisplayName("Create Project | Malformed JSON | Returns 400 Bad Request")
    void createProject_whenMalformedJson_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/projects").contentType(MediaType.APPLICATION_JSON).content("{invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.request").value("Invalid JSON input format"));

        verifyNoInteractions(createProjectService);
    }

    /* ---------- Get All Projects ---------- */
    @Test
    @DisplayName("Get All Projects | Projects exists | Returns 200 and projects")
    void getAllProjects_whenProjectsExists_shouldReturnProjects() throws Exception {
        // Arrange
        var response = new GetAllProjectsResponseDto(
                1L,
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("100"),
                null
        );
        when(getAllProjectsService.execute()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [{
                        "id": 1,
                        "name": "Example",
                        "description": "Description",
                        "workedHours": 10,
                        "startDate": "2026-01-01",
                        "dueDate": "2026-01-30",
                        "projectStatus": "IN_PROGRESS",
                        "clientId": 1,
                        "projectType": "FIXED_PRICE",
                        "budget": 100,
                        "hourlyRate": null
                    }]
                """));

        verify(getAllProjectsService).execute();
    }

    @Test
    @DisplayName("Get All Projects | No Projects exists | Returns 200 and empty list")
    void getAllProjects_whenNoProjectsExists_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(getAllProjectsService.execute()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(getAllProjectsService).execute();
    }

    /* ---------- Get Project by Id ---------- */
    @Test
    @DisplayName("Get Project by Id | Project exists | Returns 200 and Project")
    void getProjectById_whenProjectExists_shouldReturnProject() throws Exception {
        // Arrange
        var response = new GetProjectByIdResponseDto(
                1L,
                "Example",
                "Description",
                10,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 30),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("100"),
                null
        );
        when(getProjectByIdService.execute(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "id": 1,
                        "name": "Example",
                        "description": "Description",
                        "workedHours": 10,
                        "startDate": "2026-01-01",
                        "dueDate": "2026-01-30",
                        "projectStatus": "IN_PROGRESS",
                        "clientId": 1,
                        "projectType": "FIXED_PRICE",
                        "budget": 100,
                        "hourlyRate": null
                    }
                """));

        verify(getProjectByIdService).execute(1L);
    }

    @Test
    @DisplayName("Get Project by Id | Project does not exists | Returns 404 Not found")
    void getProjectById_whenProjectDoesNotExists_shouldReturnNotFound() throws Exception {
        // Arrange
        when(getProjectByIdService.execute(1L)).thenThrow(new EntityNotFoundException("Project not found"));

        // Act & Assert
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Project not found"));

        verify(getProjectByIdService).execute(1L);
    }

    @Test
    @DisplayName("Get Project by Id | Invalid id | Returns 400 Bad Request")
    void getProjectById_whenInvalidId_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/projects/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.id").value("Invalid value 'invalid'"));

        verifyNoInteractions(getProjectByIdService);
    }

    /* ---------- Update Project by Id ---------- */
    private static final String UPDATE_PROJECT_JSON = """
            {
                "name": "Updated Project",
                "description": "Updated project description",
                "workedHours": 100,
                "startDate": "2026-02-01",
                "dueDate": "2026-02-25"
            }
            """;
    private static final UpdateProjectByIdRequestDto updateProjectByIdRequestDto = new UpdateProjectByIdRequestDto(
            "Updated Project",
            "Updated project description",
            100,
            LocalDate.of(2026, 2, 1),
            LocalDate.of(2026, 2, 25)
    );

    @Test
    @DisplayName("Update Project by Id | Valid request | Returns 200 and updated Project")
    void updateProjectById_whenValidRequest_shouldReturnUpdatedProject() throws Exception {
        // Arrange
        var response = new UpdateProjectByIdResponseDto(
                1L,
                "Updated Project",
                "Updated project description",
                100,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 25),
                ProjectStatus.IN_PROGRESS,
                1L,
                "FIXED_PRICE",
                new BigDecimal("100"),
                null
        );
        when(updateProjectByIdService.execute(1L, updateProjectByIdRequestDto)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/projects").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_PROJECT_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "name": "Updated Project",
                            "description": "Updated project description",
                            "workedHours": 100,
                            "startDate": "2026-02-01",
                            "dueDate": "2026-02-25",
                            "projectStatus": "IN_PROGRESS",
                            "clientId": 1,
                            "projectType": "FIXED_PRICE",
                            "budget": 100,
                            "hourlyRate": null
                        }
                        """));

        verify(updateProjectByIdService).execute(1L, updateProjectByIdRequestDto);
    }


    @Test
    @DisplayName("Update Project by Id | Project Not Found | Returns 404 Not Found")
    void updateProjectById_whenProjectNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        when(updateProjectByIdService.execute(1L, updateProjectByIdRequestDto)).thenThrow(new EntityNotFoundException("Project not found"));

        // Act & Assert
        mockMvc.perform(put("/projects").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_PROJECT_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Project not found"));

        verify(updateProjectByIdService).execute(1L, updateProjectByIdRequestDto);
    }

    @Test
    @DisplayName("Update Project by Id | Start Date after Due Date | Returns 400 Bad Request")
    void updateProjectById_whenStartDateAfterDueDate_shouldReturnBadRequest() throws Exception {
        // Arrange
        String errorMessage = "Invalid date range, 'startDate' must be after 'dueDate'";
        when(updateProjectByIdService.execute(1L, updateProjectByIdRequestDto))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // Act & Assert
        mockMvc.perform(put("/projects").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_PROJECT_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));

        verify(updateProjectByIdService).execute(1L, updateProjectByIdRequestDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"\"}",
            "{\"name\":\"An\"}",
            "{\"description\":\"\"}",
            "{\"description\":\"Abed\"}",
            "{\"workedHours\":-1}",
            "{\"startDate\":\"2026-02-30\"}",
            "{\"startDate\":\"1\"}",
            "{\"dueDate\":\"2026-02-30\"}",
            "{\"dueDate\":\"1\"}",
    })
    @DisplayName("Update Project by Id | Invalid request | Returns 400 Bad Request")
    void updateProjectById_whenInvalidRequest_shouldReturnBadRequest(String invalidJson) throws Exception {
        // Act & Assert
        mockMvc.perform(put("/projects").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isMap());

        verifyNoInteractions(updateProjectByIdService);
    }

    @Test
    @DisplayName("Update Project by Id | Invalid id | Returns 400 Bad Request")
    void updateProjectById_whenInvalidId_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/projects").param("id", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.id").value("Invalid value 'invalid'"));

        verifyNoInteractions(updateProjectByIdService);
    }

    /* ---------- Update Project Status by Id ---------- */
    private static final String UPDATE_PROJECT_STATUS_BY_ID_JSON = """
            {
                "projectStatus": "COMPLETED"
            }
            """;

    private static final UpdateProjectStatusByIdRequestDto updateProjectStatusByIdRequestDto = new UpdateProjectStatusByIdRequestDto(
            ProjectStatus.COMPLETED
    );

    @Test
    @DisplayName("Update Project Status by Id | Valid Request | Returns 200 and Updated Project Status")
    void updateProjectStatusById_whenValidRequest_shouldReturn200AndUpdatedProjectStatus() throws Exception {
        // Arrange
        var response = new UpdateProjectStatusByIdResponseDto(
                1L,
                "Updated Project Status",
                ProjectStatus.COMPLETED,
                1L
        );

        when(updateProjectStatusByIdService.execute(1L, updateProjectStatusByIdRequestDto)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/projects").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_PROJECT_STATUS_BY_ID_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectStatus").value("COMPLETED"));

        verify(updateProjectStatusByIdService).execute(1L, updateProjectStatusByIdRequestDto);
    }

    @Test
    @DisplayName("Update Project Status by Id | Project Not Found | Returns 404 Not Found")
    void updateProjectStatusById_whenProjectNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        when(updateProjectStatusByIdService.execute(1L, updateProjectStatusByIdRequestDto))
                .thenThrow(new EntityNotFoundException("Project not found"));

        // Act & Assert
        mockMvc.perform(patch("/projects").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_PROJECT_STATUS_BY_ID_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Project not found"));

        verify(updateProjectStatusByIdService).execute(1L, updateProjectStatusByIdRequestDto);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"projectStatus\":\"\"}",
            "{\"projectStatus\":\"INVALID\"}",
    })
    @DisplayName("Update Project Status by Id | Invalid request | Returns 400 Bad Request")
    void updateProjectStatusById_whenInvalidRequest_shouldReturnBadRequest(String invalidJson) throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/projects").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isMap());

        verifyNoInteractions(updateProjectByIdService);
    }

    @Test
    @DisplayName("Update Project Status by Id | Invalid id | Returns 400 Bad Request")
    void updateProjectStatusById_whenInvalidId_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/projects").param("id", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.id").value("Invalid value 'invalid'"));

        verifyNoInteractions(updateProjectStatusByIdService);
    }

}
