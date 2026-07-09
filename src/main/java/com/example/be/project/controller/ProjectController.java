package com.example.be.project.controller;

import com.example.be.project.dto.request.CreateProjectRequest;
import com.example.be.project.dto.request.MetricRequest;
import com.example.be.project.dto.request.ProjectTechRequest;
import com.example.be.project.dto.request.TroubleshootingRequest;
import com.example.be.project.dto.request.UpdateProjectRequest;
import com.example.be.project.dto.response.ProjectResponse;
import com.example.be.project.dto.response.ProjectSummaryResponse;
import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.dto.result.ProjectSummaryResult;
import com.example.be.project.service.command.MetricCommand;
import com.example.be.project.service.command.ProjectCommandService;
import com.example.be.project.service.command.ProjectTechCommand;
import com.example.be.project.service.query.ProjectQueryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        TroubleshootingRequest troubleshooting = request.troubleshooting();
        ProjectResult result = projectCommandService.create(
                request.name(),
                request.slug(),
                request.summary(),
                request.category(),
                request.team(),
                request.role(),
                request.startDate(),
                request.endDate(),
                request.githubUrl(),
                request.overview(),
                request.architecture(),
                request.conclusion(),
                toMetricCommands(request.metrics()),
                troubleshooting == null ? null : troubleshooting.situationCause(),
                troubleshooting == null ? null : troubleshooting.solutions(),
                request.learnings(),
                toTechCommands(request.techStacks()),
                request.thumbnailUrl(),
                request.representativeImageUrl(),
                request.imageUrls()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectResponse.from(result));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProjectResponse> get(
            @PathVariable String slug
    ) {
        ProjectResult result = projectQueryService.getBySlug(slug);
        return ResponseEntity.ok(ProjectResponse.from(result));
    }

    @GetMapping
    public ResponseEntity<Slice<ProjectSummaryResponse>> getProjects(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Slice<ProjectSummaryResult> results = projectQueryService.getSummaries(pageable);
        return ResponseEntity.ok(results.map(ProjectSummaryResponse::from));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        TroubleshootingRequest troubleshooting = request.troubleshooting();
        ProjectResult result = projectCommandService.update(
                projectId,
                request.name(),
                request.slug(),
                request.summary(),
                request.category(),
                request.team(),
                request.role(),
                request.startDate(),
                request.endDate(),
                request.githubUrl(),
                request.overview(),
                request.architecture(),
                request.conclusion(),
                toMetricCommands(request.metrics()),
                troubleshooting == null ? null : troubleshooting.situationCause(),
                troubleshooting == null ? null : troubleshooting.solutions(),
                request.learnings(),
                toTechCommands(request.techStacks()),
                request.thumbnailUrl(),
                request.representativeImageUrl(),
                request.imageUrls()
        );
        return ResponseEntity.ok(ProjectResponse.from(result));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId
    ) {
        projectCommandService.delete(projectId);
        return ResponseEntity.noContent().build();
    }

    private List<MetricCommand> toMetricCommands(List<MetricRequest> metrics) {
        if (metrics == null) {
            return List.of();
        }
        return metrics.stream()
                .map(metric -> new MetricCommand(metric.label(), metric.before(), metric.after(), metric.description()))
                .toList();
    }

    private List<ProjectTechCommand> toTechCommands(List<ProjectTechRequest> techStacks) {
        if (techStacks == null) {
            return List.of();
        }
        return techStacks.stream()
                .map(tech -> new ProjectTechCommand(tech.category(), tech.technology(), tech.purpose()))
                .toList();
    }
}
