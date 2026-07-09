package com.example.be.experience.dto.response;

import com.example.be.experience.dto.result.ExperienceResult;
import java.util.List;

/**
 * 경력/학력 API 응답. 유형은 문자열(enum name)로, 날짜는 문자열로 직렬화한다.
 * 종료일이 없는 단일 시점 항목은 {@code endDate}가 null이다. 소속은 프론트 계약에 맞춰 {@code org}로 노출한다.
 */
public record ExperienceResponse(
        Long id,
        String type,
        String title,
        String org,
        String startDate,
        String endDate,
        List<String> points,
        boolean highlighted,
        int sortOrder
) {
    public static ExperienceResponse from(ExperienceResult result) {
        return new ExperienceResponse(
                result.id(),
                result.type().name(),
                result.title(),
                result.organization(),
                result.startDate() == null ? null : result.startDate().toString(),
                result.endDate() == null ? null : result.endDate().toString(),
                result.points(),
                result.highlighted(),
                result.sortOrder()
        );
    }
}
