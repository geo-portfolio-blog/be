package com.example.be.experience.dto.request;

import com.example.be.experience.domain.ExperienceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * 경력/학력 생성 요청. 종료일은 단일 시점 항목을 위해 선택이다.
 * 클라이언트가 제어해선 안 되는 식별자 필드는 포함하지 않는다.
 */
public record CreateExperienceRequest(
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
