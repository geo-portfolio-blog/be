package com.example.be.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 프로젝트 Tech Stack 표 한 행 입력(분류 · 기술 · 용도).
 */
public record ProjectTechRequest(
        @NotBlank
        @Size(max = 100)
        String category,

        @NotBlank
        @Size(max = 200)
        String technology,

        @Size(max = 500)
        String purpose
) {
}
