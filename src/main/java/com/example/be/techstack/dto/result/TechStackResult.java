package com.example.be.techstack.dto.result;

import com.example.be.techstack.domain.TechStack;
import com.example.be.techstack.domain.TechStackCategory;

/**
 * 기술 스택 결과. Command Service가 방금 저장한 Entity 변환, Query Service의 목록 조회
 * QueryDSL Projection에서 모두 사용한다. QueryDSL {@code Projections.constructor}로 직접
 * Projection하므로 생성자 인자 순서는 이 record의 컴포넌트 순서와 일치해야 한다.
 */
public record TechStackResult(
        Long id,
        String name,
        TechStackCategory category,
        String imageUrl,
        int proficiency
) {
    public static TechStackResult from(TechStack techStack) {
        return new TechStackResult(
                techStack.getId(),
                techStack.getName(),
                techStack.getCategory(),
                techStack.getImageUrl(),
                techStack.getProficiency()
        );
    }
}
