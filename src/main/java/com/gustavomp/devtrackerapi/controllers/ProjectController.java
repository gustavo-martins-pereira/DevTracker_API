package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectStatusByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.*;
import com.gustavomp.devtrackerapi.models.enums.ProjectStatus;
import com.gustavomp.devtrackerapi.services.client.*;
import com.gustavomp.devtrackerapi.services.project.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/projects")
@AllArgsConstructor
public class ProjectController {

    private final CreateProjectService createProjectService;
    private final GetAllProjectsService getAllProjectsService;
    private final GetProjectByIdService getProjectByIdService;
    private final UpdateProjectByIdService updateProjectByIdService;
    private final UpdateProjectStatusByIdService updateProjectStatusByIdService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateProjectResponseDto> createProject(@RequestBody @Valid CreateProjectRequestDto createProjectRequestDto) {
        CreateProjectResponseDto createProjectResponseDto = createProjectService.execute(createProjectRequestDto);

        return new ResponseEntity<>(createProjectResponseDto, HttpStatus.CREATED);
    }

    /* ---------- GET ---------- */
    @GetMapping
    public ResponseEntity<List<GetAllProjectsResponseDto>> getAllProjects(@RequestParam(required = false) ProjectStatus projectStatus) {
        if (projectStatus != null) {
            return ResponseEntity.ok(getAllProjectsService.execute(projectStatus));
        }

        return ResponseEntity.ok(getAllProjectsService.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProjectByIdResponseDto> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(getProjectByIdService.execute(id));
    }

    /* ---------- PUT ---------- */
    @PutMapping(params = "id")
    public ResponseEntity<UpdateProjectByIdResponseDto> updateProjectById(@RequestParam Long id, @RequestBody @Valid UpdateProjectByIdRequestDto updateProjectByIdRequestDto) {
        UpdateProjectByIdResponseDto updateProjectByIdResponseDto = updateProjectByIdService.execute(id, updateProjectByIdRequestDto);

        return ResponseEntity.ok(updateProjectByIdResponseDto);
    }

    /* ---------- PATCH ---------- */
    @PatchMapping(params = "id")
    public ResponseEntity<UpdateProjectStatusByIdResponseDto> updateProjectStatusById(@RequestParam Long id, @RequestBody @Valid UpdateProjectStatusByIdRequestDto updateProjectStatusByIdRequestDto) {
        UpdateProjectStatusByIdResponseDto updateProjectStatusByIdResponseDto = updateProjectStatusByIdService.execute(id, updateProjectStatusByIdRequestDto);

        return ResponseEntity.ok(updateProjectStatusByIdResponseDto);
    }

}
