package com.example.be.project.dto.response;

import com.example.be.project.dto.result.ProjectSummaryResult;

/**
 * 프로젝트 목록 항목 API 응답. 대표 사진(미니), 프로젝트명, 한 줄 소개,
 * 개발 인원 총 수, 맡은 부분, 개발 기간 등 요약 정보만 담는다.
 */
public record ProjectSummaryResponse(
        Long id,
        String name,
        String summary,
        String thumbnailUrl,
        int memberCount,
        String contribution,
        String startDate,
        String endDate
) {
    public static ProjectSummaryResponse from(ProjectSummaryResult result) {
        return new ProjectSummaryResponse(
                result.id(),
                result.name(),
                result.summary(),
                result.thumbnailUrl(),
                result.memberCount(),
                result.contribution(),
                result.startDate() == null ? null : result.startDate().toString(),
                result.endDate() == null ? null : result.endDate().toString()
        );
    }
}
