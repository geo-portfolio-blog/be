package com.example.be.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * 프로젝트 생성 요청. 이미지는 실제 업로드가 아닌 저장된 URL(또는 키) 문자열로 받는다고 가정한다.
 * 클라이언트가 제어해선 안 되는 식별자/소유자 필드는 포함하지 않는다.
 */
public record CreateProjectRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 200)
        String summary,

        @NotEmpty
        @Size(max = 20)
        List<@NotBlank @Size(max = 100) String> members,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @Size(max = 10000)
        String overview,

        @Size(max = 10000)
        String contribution,

        @Size(max = 10000)
        String conclusion,

        @Size(max = 10000)
        String troubleshooting,

        @Size(max = 30)
        List<@NotBlank @Size(max = 100) String> techStacks,

        @Size(max = 1000)
        String thumbnailUrl,

        @Size(max = 1000)
        String representativeImageUrl,

        @Size(max = 20)
        List<@Size(max = 1000) String> imageUrls
) {
}
