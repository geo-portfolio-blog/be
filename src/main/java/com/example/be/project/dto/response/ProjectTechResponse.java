package com.example.be.project.dto.response;

import com.example.be.project.dto.result.ProjectTechResult;

/**
 * 프로젝트 Tech Stack 표 한 행 API 응답(분류 · 기술 · 용도).
 */
public record ProjectTechResponse(
        String category,
        String technology,
        String purpose
) {
    public static ProjectTechResponse from(ProjectTechResult result) {
        return new ProjectTechResponse(
                result.category(),
                result.technology(),
                result.purpose()
        );
    }
}
