package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.project.CreateProjectResponseDto;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.models.Client;
import com.gustavomp.devtrackerapi.models.Project;
import com.gustavomp.devtrackerapi.repositories.ClientRepository;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateProjectService {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final ProjectMapper projectMapper;

    public CreateProjectResponseDto execute(CreateProjectRequestDto createProjectRequestDto) {
        Client client = clientRepository.findById(createProjectRequestDto.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client with id " + createProjectRequestDto.clientId() + " not found"));

        Optional<Project> existingProject = projectRepository.findByName(createProjectRequestDto.name());
        if (existingProject.isPresent()) {
            throw new EntityNotFoundException("Project with name " + createProjectRequestDto.name() + " already exists");
        }

        Project project = projectMapper.toEntity(createProjectRequestDto);
        project.setClient(client);

        Project savedProject = projectRepository.save(project);

        return projectMapper.toCreateProjectResponseDto(savedProject);
    }

}
