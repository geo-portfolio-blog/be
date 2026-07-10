package com.example.be.techpart.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Technical Expertise의 개별 스킬 값 객체. 스킬명({@code name})과 부가 설명({@code note}) 두 스칼라
 * 속성만 가진다. 자체 식별자/생명주기가 없으므로 {@link TechPart} 애그리거트가 소유하는 값 객체로 두고,
 * Part의 {@code @ElementCollection}으로 순서를 보존해 저장한다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechSkill {

    @Column(name = "skill_name", nullable = false, length = 100)
    private String name;

    /** 스킬 아래에 노출하는 부가 설명. */
    @Column(name = "note", nullable = false, length = 500)
    private String note;

    private TechSkill(String name, String note) {
        validate(name, note);
        this.name = name;
        this.note = note;
    }

    public static TechSkill of(String name, String note) {
        return new TechSkill(name, note);
    }

    private void validate(String name, String note) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("스킬명은 필수입니다.");
        }
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("스킬 설명은 필수입니다.");
        }
    }
}
