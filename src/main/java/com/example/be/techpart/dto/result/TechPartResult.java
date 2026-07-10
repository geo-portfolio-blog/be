package com.example.be.techpart.dto.result;

import com.example.be.techpart.domain.TechPart;
import java.util.List;

/**
 * 기술 분류(Part) 결과. Command Service가 방금 저장/수정한 Entity, Query Service가 fetch join으로
 * 조회한 Entity를 서비스 경계 밖으로 전달하기 위한 변환 결과다. 스킬은 {@code @OrderColumn} 순서를 그대로 옮긴다.
 */
public record TechPartResult(
        Long id,
        String name,
        int sortOrder,
        List<TechSkillResult> skills
) {
    public static TechPartResult from(TechPart part) {
        return new TechPartResult(
                part.getId(),
                part.getName(),
                part.getSortOrder(),
                part.getSkills().stream().map(TechSkillResult::from).toList()
        );
    }
}
