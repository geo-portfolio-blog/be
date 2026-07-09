package com.example.be.experience.dto.request;

import com.example.be.experience.domain.ExperienceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * 경력/학력 수정 요청(PUT 의미: 항목 전체를 새 값으로 교체). 입력 형식 검증 규칙은 생성 요청과 같다.
 */
public record UpdateExperienceRequest(
        @NotNull
        ExperienceType type,

        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 200)
        String organization,

        @NotNull
        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 20)
        List<@NotBlank @Size(max = 1000) String> points,

        boolean highlighted,

        int sortOrder
) {
}
