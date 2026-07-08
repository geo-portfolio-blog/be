package com.example.be.project.dto.result;

import java.time.LocalDate;

/**
 * 프로젝트 목록 조회용 요약 결과. QueryDSL {@code Projections.constructor}로 직접 Projection한다.
 * 생성자 인자 순서는 이 record의 컴포넌트 순서와 반드시 일치해야 한다.
 *
 * <p>목록 화면 요구사항: 대표 사진(미니) + 한 줄 소개 + 개발 인원 총 수 + 맡은 부분 + 개발 기간.
 */
public record ProjectSummaryResult(
        Long id,
        String name,
        String summary,
        String thumbnailUrl,
        int memberCount,
        String contribution,
        LocalDate startDate,
        LocalDate endDate
) {
}
