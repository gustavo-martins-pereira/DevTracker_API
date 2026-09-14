package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.requests.project.UpdateProjectByIdRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.UpdateProjectByIdResponseDto;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateProjectByIdService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public UpdateProjectByIdResponseDto execute(Long id, UpdateProjectByIdRequestDto updateProjectByIdRequestDto) {
        Optional<Project> oldProject = projectRepository.findById(id);
        if (oldProject.isEmpty()) throw new EntityNotFoundException("Project with id " + id + " not found");

        if (updateProjectByIdRequestDto.startDate().isAfter(updateProjectByIdRequestDto.dueDate())) {
            throw new IllegalArgumentException("Invalid date range, 'startDate' must be after 'dueDate'");
        }

        Project project = projectMapper.toEntity(updateProjectByIdRequestDto, oldProject.get());

        Project updatedClient = projectRepository.save(project);

        return projectMapper.toUpdateProjectByIdResponseDto(updatedClient);
    }

}
