package com.example.be.project.dto.result;

import com.example.be.project.domain.ProjectCategory;
import java.time.LocalDate;
import java.util.List;

/**
 * 프로젝트 목록 카드 요약 결과.
 *
 * <p>카드 요구사항: 카테고리 + 썸네일 + 제목 + 한 줄 소개 + 역할 + 태그 + 기간. 스칼라 본문은 QueryDSL
 * Projection으로, 태그(tags)는 페이지 프로젝트 id로 좁힌 별도 쿼리로 Tech Stack의 기술명을 모아 조립한다.
 * 그래서 이 record는 단일 Projection 생성자 대상이 아니다.
 */
public record ProjectSummaryResult(
        Long id,
        String name,
        String slug,
        String summary,
        ProjectCategory category,
        String thumbnailUrl,
        String role,
        List<String> tags,
        LocalDate startDate,
        LocalDate endDate
) {
}
