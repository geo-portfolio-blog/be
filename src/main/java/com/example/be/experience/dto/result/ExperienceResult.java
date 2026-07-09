package com.example.be.experience.dto.result;

import com.example.be.experience.domain.Experience;
import com.example.be.experience.domain.ExperienceType;
import java.time.LocalDate;
import java.util.List;

/**
 * 경력/학력 결과. Command Service가 방금 저장/수정한 Entity, Query Service가 조회한 Entity를
 * 서비스 경계 밖으로 전달하기 위한 변환 결과다.
 */
public record ExperienceResult(
        Long id,
        ExperienceType type,
        String title,
        String organization,
        LocalDate startDate,
        LocalDate endDate,
        List<String> points,
        boolean highlighted,
        int sortOrder
) {
    public static ExperienceResult from(Experience experience) {
        return new ExperienceResult(
                experience.getId(),
                experience.getType(),
                experience.getTitle(),
                experience.getOrganization(),
                experience.getPeriod().getStartDate(),
                experience.getPeriod().getEndDate(),
                List.copyOf(experience.getPoints()),
                experience.isHighlighted(),
                experience.getSortOrder()
        );
    }
}
