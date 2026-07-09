package com.example.be.project.repository;

import com.example.be.project.domain.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

    Optional<Project> findBySlug(String slug);
}
