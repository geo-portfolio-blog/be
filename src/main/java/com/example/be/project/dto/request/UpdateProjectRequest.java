package com.example.be.project.dto.request;

import com.example.be.project.domain.ProjectCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * 프로젝트 수정 요청(PUT 의미: 프로젝트 전체를 새 값으로 교체). 입력 형식 검증 규칙은 생성 요청과 같다.
 */
public record UpdateProjectRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 200)
        String slug,

        @Size(max = 200)
        String summary,

        @NotNull
        ProjectCategory category,

        @Size(max = 100)
        String team,

        @Size(max = 100)
        String role,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @Size(max = 1000)
        String githubUrl,

        @Size(max = 10000)
        String overview,

        @Size(max = 10000)
        String architecture,

        @Size(max = 10000)
        String conclusion,

        @Size(max = 20)
        List<@Valid MetricRequest> metrics,

        @Valid
        TroubleshootingRequest troubleshooting,

        @Size(max = 30)
        List<@NotBlank @Size(max = 1000) String> learnings,

        @Size(max = 30)
        List<@Valid ProjectTechRequest> techStacks,

        @Size(max = 1000)
        String thumbnailUrl,

        @Size(max = 1000)
        String representativeImageUrl,

        @Size(max = 20)
        List<@Size(max = 1000) String> imageUrls
) {
}
