package com.example.be.techstack.dto.response;

import com.example.be.techstack.dto.result.TechStackResult;

/**
 * 기술 스택 API 응답. 분류는 문자열(enum name)로 표현하여 클라이언트가 카테고리별로 묶어 노출할 수 있게 한다.
 * 이미지가 없는 스킬은 {@code imageUrl}이 null이다.
 */
public record TechStackResponse(
        Long id,
        String name,
        String category,
        String note,
        String imageUrl,
        int sortOrder
) {
    public static TechStackResponse from(TechStackResult result) {
        return new TechStackResponse(
                result.id(),
                result.name(),
                result.category().name(),
                result.note(),
                result.imageUrl(),
                result.sortOrder()
        );
    }
}
