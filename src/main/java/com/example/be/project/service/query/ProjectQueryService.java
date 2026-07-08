package com.example.be.project.service.query;

import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.dto.result.ProjectSummaryResult;
import com.example.be.project.exception.ProjectNotFoundException;
import com.example.be.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryService {

    private final ProjectRepository projectRepository;

    public ProjectResult get(Long projectId) {
        return projectRepository.findResultById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    public Page<ProjectSummaryResult> getSummaries(Pageable pageable) {
        return projectRepository.findSummaries(pageable);
    }
}
