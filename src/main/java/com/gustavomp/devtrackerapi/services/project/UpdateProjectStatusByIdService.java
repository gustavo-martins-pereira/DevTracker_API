package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectStatusByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.UpdateProjectStatusByIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateProjectStatusByIdService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public UpdateProjectStatusByIdResponseDto execute(Long id, UpdateProjectStatusByIdRequestDto updateProjectStatusByIdRequestDto) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) throw new EntityNotFoundException("Project with id " + id + " not found");

        Project updatedProject = projectRepository.save(
                projectMapper.toEntity(updateProjectStatusByIdRequestDto, project.get())
        );

        return projectMapper.toUpdateProjectStatusByIdResponseDto(updatedProject);
    }

}
