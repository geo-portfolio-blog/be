package com.example.be.project.repository;

import com.example.be.project.dto.result.ProjectSummaryResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProjectRepositoryCustom {

    Slice<ProjectSummaryResult> findSummaries(Pageable pageable);
}
