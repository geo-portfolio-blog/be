package com.example.be.techstack.dto.request;

import com.example.be.techstack.domain.TechStackCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 기술 스택 수정 요청(PUT 의미: 항목 전체를 새 값으로 교체). 입력 형식 검증 규칙은 생성 요청과 같다.
 */
public record UpdateTechStackRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        TechStackCategory category,

        @NotBlank
        @Size(max = 500)
        String note,

        @Size(max = 1000)
        String imageUrl,

        int sortOrder
) {
}
