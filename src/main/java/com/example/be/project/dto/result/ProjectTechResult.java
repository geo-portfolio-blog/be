package com.example.be.project.dto.result;

import com.example.be.project.domain.ProjectTech;

/**
 * 프로젝트 Tech Stack 표 한 행 결과.
 */
public record ProjectTechResult(
        String category,
        String technology,
        String purpose
) {
    public static ProjectTechResult from(ProjectTech tech) {
        return new ProjectTechResult(
                tech.getCategory(),
                tech.getTechnology(),
                tech.getPurpose()
        );
    }
}
