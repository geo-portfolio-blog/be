package com.example.be.project.dto.response;

import com.example.be.project.dto.result.ProjectResult;
import java.util.List;

/**
 * 프로젝트 단건 상세 API 응답. 카테고리는 문자열(enum name), 날짜는 문자열로 직렬화하고,
 * 지표·Troubleshooting·Tech Stack은 별도 응답 객체로 노출한다. 값이 없는 섹션은 빈 목록/누락으로 나간다.
 */
public record ProjectResponse(
        Long id,
        String name,
        String slug,
        String summary,
        String category,
        String team,
        String role,
        String startDate,
        String endDate,
        String githubUrl,
        String overview,
        String architecture,
        String conclusion,
        List<MetricResponse> metrics,
        TroubleshootingResponse troubleshooting,
        List<String> learnings,
        List<ProjectTechResponse> techStacks,
        String representativeImageUrl,
        List<String> imageUrls
) {
    public static ProjectResponse from(ProjectResult result) {
        return new ProjectResponse(
                result.id(),
                result.name(),
                result.slug(),
                result.summary(),
                result.category().name(),
                result.team(),
                result.role(),
                result.startDate() == null ? null : result.startDate().toString(),
                result.endDate() == null ? null : result.endDate().toString(),
                result.githubUrl(),
                result.overview(),
                result.architecture(),
                result.conclusion(),
                result.metrics().stream().map(MetricResponse::from).toList(),
                result.troubleshooting() == null ? null : TroubleshootingResponse.from(result.troubleshooting()),
                result.learnings(),
                result.techStacks().stream().map(ProjectTechResponse::from).toList(),
                result.representativeImageUrl(),
                result.imageUrls()
        );
    }
}
