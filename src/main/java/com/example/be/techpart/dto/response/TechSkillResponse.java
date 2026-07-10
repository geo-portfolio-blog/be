package com.example.be.techpart.dto.response;

import com.example.be.techpart.dto.result.TechSkillResult;

/**
 * 스킬 API 응답 표면. 이름과 설명(note)을 그대로 노출한다.
 */
public record TechSkillResponse(
        String name,
        String note
) {
    public static TechSkillResponse from(TechSkillResult result) {
        return new TechSkillResponse(result.name(), result.note());
    }
}
