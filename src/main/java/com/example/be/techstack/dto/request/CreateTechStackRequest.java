package com.example.be.techstack.dto.request;

import com.example.be.techstack.domain.TechStackCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 기술 스택 등록 요청. 실력 점수는 1~5 범위를 형식 검증으로 강제한다.
 * 이미지는 실제 업로드가 아닌 저장된 URL 문자열로 받는다고 가정한다.
 */
public record CreateTechStackRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        TechStackCategory category,

        @NotBlank
        @Size(max = 1000)
        String imageUrl,

        @Min(1)
        @Max(5)
        int proficiency
) {
}
