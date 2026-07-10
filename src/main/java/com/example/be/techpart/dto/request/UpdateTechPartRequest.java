package com.example.be.techpart.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 기술 분류(Part) 수정 요청. PUT(전체 교체) 의미이므로 스킬 목록도 통째로 새 값으로 교체한다.
 */
public record UpdateTechPartRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        int sortOrder,

        @Size(max = 50)
        List<@Valid TechSkillRequest> skills
) {
}
