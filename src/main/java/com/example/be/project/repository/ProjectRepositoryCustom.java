package com.example.be.project.repository;

import com.example.be.project.dto.result.ProjectSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {

    Page<ProjectSummaryResult> findSummaries(Pageable pageable);
}
