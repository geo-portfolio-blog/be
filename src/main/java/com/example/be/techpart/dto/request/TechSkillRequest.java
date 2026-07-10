package com.example.be.techpart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Part에 속한 스킬 한 개 입력(이름 + 설명). Part 생성/수정 요청의 skills 원소로 검증된다.
 */
public record TechSkillRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 500)
        String note
) {
}
