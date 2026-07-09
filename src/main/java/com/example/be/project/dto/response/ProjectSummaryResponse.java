package com.example.be.project.dto.response;

import com.example.be.project.dto.result.ProjectSummaryResult;
import java.util.List;

/**
 * 프로젝트 목록 카드 API 응답. 카테고리·썸네일·제목·슬러그·소개·역할·태그·기간을 담는다.
 */
public record ProjectSummaryResponse(
        Long id,
        String name,
        String slug,
        String summary,
        String category,
        String thumbnailUrl,
        String role,
        List<String> tags,
        String startDate,
        String endDate
) {
    public static ProjectSummaryResponse from(ProjectSummaryResult result) {
        return new ProjectSummaryResponse(
                result.id(),
                result.name(),
                result.slug(),
                result.summary(),
                result.category().name(),
                result.thumbnailUrl(),
                result.role(),
                result.tags(),
                result.startDate() == null ? null : result.startDate().toString(),
                result.endDate() == null ? null : result.endDate().toString()
        );
    }
}
