package com.example.be.project.service.command;

import com.example.be.project.domain.DevelopmentPeriod;
import com.example.be.project.domain.Metric;
import com.example.be.project.domain.Project;
import com.example.be.project.domain.ProjectCategory;
import com.example.be.project.domain.ProjectTech;
import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.exception.ProjectNotFoundException;
import com.example.be.project.repository.ProjectRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommandService {

    private final ProjectRepository projectRepository;

    public ProjectResult create(
            String name,
            String slug,
            String summary,
            ProjectCategory category,
            String team,
            String role,
            LocalDate startDate,
            LocalDate endDate,
            String githubUrl,
            String overview,
            String architecture,
            String conclusion,
            List<MetricCommand> metrics,
            String troubleshootingSituation,
            List<String> troubleshootingSolutions,
            List<String> learnings,
            List<ProjectTechCommand> techStacks,
            String thumbnailUrl,
            String representativeImageUrl,
            List<String> imageUrls
    ) {
        Project project = Project.create(
                name,
                slug,
                summary,
                category,
                team,
                role,
                DevelopmentPeriod.of(startDate, endDate),
                githubUrl,
                overview,
                architecture,
                conclusion,
                toMetrics(metrics),
                troubleshootingSituation,
                troubleshootingSolutions,
                learnings,
                toTechStacks(techStacks),
                thumbnailUrl,
                representativeImageUrl,
                imageUrls
        );
        Project saved = projectRepository.save(project);
        return ProjectResult.from(saved);
    }

    public ProjectResult update(
            Long projectId,
            String name,
            String slug,
            String summary,
            ProjectCategory category,
            String team,
            String role,
            LocalDate startDate,
            LocalDate endDate,
            String githubUrl,
            String overview,
            String architecture,
            String conclusion,
            List<MetricCommand> metrics,
            String troubleshootingSituation,
            List<String> troubleshootingSolutions,
            List<String> learnings,
            List<ProjectTechCommand> techStacks,
            String thumbnailUrl,
            String representativeImageUrl,
            List<String> imageUrls
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        project.update(
                name,
                slug,
                summary,
                category,
                team,
                role,
                DevelopmentPeriod.of(startDate, endDate),
                githubUrl,
                overview,
                architecture,
                conclusion,
                toMetrics(metrics),
                troubleshootingSituation,
                troubleshootingSolutions,
                learnings,
                toTechStacks(techStacks),
                thumbnailUrl,
                representativeImageUrl,
                imageUrls
        );
        return ProjectResult.from(project);
    }

    public void delete(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }
        projectRepository.deleteById(projectId);
    }

    private List<Metric> toMetrics(List<MetricCommand> metrics) {
        if (metrics == null) {
            return List.of();
        }
        return metrics.stream()
                .map(metric -> Metric.of(metric.label(), metric.before(), metric.after(), metric.description()))
                .toList();
    }

    private List<ProjectTech> toTechStacks(List<ProjectTechCommand> techStacks) {
        if (techStacks == null) {
            return List.of();
        }
        return techStacks.stream()
                .map(tech -> ProjectTech.of(tech.category(), tech.technology(), tech.purpose()))
                .toList();
    }
}
