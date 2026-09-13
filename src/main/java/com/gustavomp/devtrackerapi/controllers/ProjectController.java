package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.CreateProjectResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.GetAllProjectsResponseDto;
import com.gustavomp.devtrackerapi.services.client.*;
import com.gustavomp.devtrackerapi.services.project.CreateProjectService;
import com.gustavomp.devtrackerapi.services.project.GetAllProjectsService;
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

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateProjectResponseDto> createProject(@RequestBody @Valid CreateProjectRequestDto createProjectRequestDto) {
        CreateProjectResponseDto createProjectResponseDto = createProjectService.execute(createProjectRequestDto);

        return new ResponseEntity<>(createProjectResponseDto, HttpStatus.CREATED);
    }

    /* ---------- GET ---------- */
    @GetMapping
    public ResponseEntity<List<GetAllProjectsResponseDto>> getAllProjects() {
        return ResponseEntity.ok(getAllProjectsService.execute());
    }

}
