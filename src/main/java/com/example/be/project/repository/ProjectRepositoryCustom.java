package com.example.be.project.repository;

import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.dto.result.ProjectSummaryResult;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {

    Optional<ProjectResult> findResultById(Long projectId);

    Page<ProjectSummaryResult> findSummaries(Pageable pageable);
}
