package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.responses.project.GetProjectByIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProjectByIdService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public GetProjectByIdResponseDto execute(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + id + " not found"));

        return projectMapper.toGetProjectByIdResponseDto(project);
    }

}
