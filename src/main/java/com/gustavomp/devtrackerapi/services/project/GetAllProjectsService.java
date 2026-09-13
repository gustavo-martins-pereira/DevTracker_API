package com.gustavomp.devtrackerapi.services.project;

import com.gustavomp.devtrackerapi.dtos.responses.project.GetAllProjectsResponseDto;
import com.gustavomp.devtrackerapi.mappers.ProjectMapper;
import com.gustavomp.devtrackerapi.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllProjectsService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public List<GetAllProjectsResponseDto> execute() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toAllProjectsResponseDto)
                .toList();
    }

}
