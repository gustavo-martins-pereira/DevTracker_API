package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.CreateProjectResponseDto;
import com.gustavomp.devtrackerapi.services.client.*;
import com.gustavomp.devtrackerapi.services.project.CreateProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/projects")
@AllArgsConstructor
public class ProjectController {

    private final CreateProjectService createProjectService;

    /* ---------- POST ---------- */
    @PostMapping
    public ResponseEntity<CreateProjectResponseDto> createProject(@RequestBody @Valid CreateProjectRequestDto createProjectRequestDto) {
        CreateProjectResponseDto createProjectResponseDto = createProjectService.execute(createProjectRequestDto);

        return new ResponseEntity<>(createProjectResponseDto, HttpStatus.CREATED);
    }

}
