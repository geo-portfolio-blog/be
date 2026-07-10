package com.example.be.techpart.dto.response;

import com.example.be.techpart.dto.result.TechPartResult;
import java.util.List;

/**
 * 기술 분류(Part) API 응답. FE의 Technical Expertise 카드(분류명 + 스킬 목록) 구조와 1:1로 대응한다.
 */
public record TechPartResponse(
        Long id,
        String name,
        int sortOrder,
        List<TechSkillResponse> skills
) {
    public static TechPartResponse from(TechPartResult result) {
        return new TechPartResponse(
                result.id(),
                result.name(),
                result.sortOrder(),
                result.skills().stream().map(TechSkillResponse::from).toList()
        );
    }
}
