package com.gustavomp.devtrackerapi.repositories;

import com.gustavomp.devtrackerapi.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Optional<Project> findByName(String name);

}
