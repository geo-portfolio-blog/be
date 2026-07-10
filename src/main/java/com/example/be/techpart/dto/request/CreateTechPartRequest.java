package com.example.be.techpart.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 기술 분류(Part) 생성 요청. 분류명과 노출 순서, 그리고 그 분류에 속한 스킬 목록을 함께 받는다.
 * 클라이언트가 제어해선 안 되는 식별자 필드는 포함하지 않는다.
 */
public record CreateTechPartRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        int sortOrder,

        @Size(max = 50)
        List<@Valid TechSkillRequest> skills
) {
}
