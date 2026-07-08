package com.example.be.project.controller;

import com.example.be.project.dto.request.CreateProjectRequest;
import com.example.be.project.dto.response.ProjectResponse;
import com.example.be.project.dto.response.ProjectSummaryResponse;
import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.dto.result.ProjectSummaryResult;
import com.example.be.project.service.command.ProjectCommandService;
import com.example.be.project.service.query.ProjectQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectCommandService projectCommandService;
    private final ProjectQueryService projectQueryService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request
    ) {
        ProjectResult result = projectCommandService.create(
                request.name(),
                request.summary(),
                request.members(),
                request.startDate(),
                request.endDate(),
                request.overview(),
                request.contribution(),
                request.conclusion(),
                request.troubleshooting(),
                request.techStacks(),
                request.thumbnailUrl(),
                request.representativeImageUrl(),
                request.imageUrls()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectResponse.from(result));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> get(
            @PathVariable Long projectId
    ) {
        ProjectResult result = projectQueryService.get(projectId);
        return ResponseEntity.ok(ProjectResponse.from(result));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectSummaryResponse>> getProjects(
            Pageable pageable
    ) {
        Page<ProjectSummaryResult> results = projectQueryService.getSummaries(pageable);
        return ResponseEntity.ok(results.map(ProjectSummaryResponse::from));
    }
}
