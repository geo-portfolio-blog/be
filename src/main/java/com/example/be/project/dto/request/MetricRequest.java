package com.example.be.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 결론 지표(before → after) 입력. 프로젝트 생성/수정 요청의 metrics 원소로 검증된다.
 */
public record MetricRequest(
        @NotBlank
        @Size(max = 100)
        String label,

        @NotBlank
        @Size(max = 200)
        String before,

        @NotBlank
        @Size(max = 200)
        String after,

        @Size(max = 500)
        String description
) {
}
