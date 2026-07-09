package com.example.be.project.dto.result;

import com.example.be.project.domain.Project;
import com.example.be.project.domain.ProjectCategory;
import java.time.LocalDate;
import java.util.List;

/**
 * 프로젝트 단건 상세 결과. Command Service가 방금 저장/수정한 Entity, Query Service가 조회한 Entity를
 * 서비스 경계 밖으로 전달하기 위한 변환 결과다. 순서가 있는 컬렉션(metrics/troubleshooting/learnings/
 * techStacks)은 엔티티의 {@code @OrderColumn} 순서를 그대로 옮긴다.
 */
public record ProjectResult(
        Long id,
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
        List<MetricResult> metrics,
        TroubleshootingResult troubleshooting,
        List<String> learnings,
        List<ProjectTechResult> techStacks,
        String representativeImageUrl,
        List<String> imageUrls
) {
    public static ProjectResult from(Project project) {
        return new ProjectResult(
                project.getId(),
                project.getName(),
                project.getSlug(),
                project.getSummary(),
                project.getCategory(),
                project.getTeam(),
                project.getRole(),
                project.getPeriod().getStartDate(),
                project.getPeriod().getEndDate(),
                project.getGithubUrl(),
                project.getOverview(),
                project.getArchitecture(),
                project.getConclusion(),
                project.getMetrics().stream().map(MetricResult::from).toList(),
                TroubleshootingResult.from(project.getTroubleshootingSituation(), project.getTroubleshootingSolutions()),
                List.copyOf(project.getLearnings()),
                project.getTechStacks().stream().map(ProjectTechResult::from).toList(),
                project.getRepresentativeImageUrl(),
                project.getExtraImageUrls()
        );
    }
}
