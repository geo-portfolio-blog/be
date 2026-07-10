package com.example.be.techpart.dto.result;

import com.example.be.techpart.domain.TechSkill;

/**
 * 스킬 결과. Part 애그리거트를 Result로 변환할 때 각 스킬 값 객체를 옮긴 것이다.
 */
public record TechSkillResult(
        String name,
        String note
) {
    public static TechSkillResult from(TechSkill skill) {
        return new TechSkillResult(skill.getName(), skill.getNote());
    }
}
