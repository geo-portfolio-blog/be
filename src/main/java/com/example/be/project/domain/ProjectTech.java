package com.example.be.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 상세 "Tech Stack" 표의 한 행을 나타내는 값 객체.
 *
 * <p>분류(category) · 기술(technology) · 용도(purpose) 3열 구조다. 이력 화면의 기술 역량
 * ({@code com.example.be.techstack} 도메인)과는 목적이 달라 재사용하지 않고, 프로젝트 애그리거트가
 * 소유하는 별도 값 객체로 둔다. 여기의 {@code category}는 표 안의 그룹 라벨("Cloud Infrastructure",
 * "CI/CD" 등)이며 프로젝트 자체의 {@link ProjectCategory}와는 무관하다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTech {

    @Column(name = "tech_category", nullable = false, length = 100)
    private String category;

    @Column(name = "technology", nullable = false, length = 200)
    private String technology;

    @Column(name = "purpose", length = 500)
    private String purpose;

    private ProjectTech(String category, String technology, String purpose) {
        validate(category, technology);
        this.category = category;
        this.technology = technology;
        this.purpose = purpose;
    }

    public static ProjectTech of(String category, String technology, String purpose) {
        return new ProjectTech(category, technology, purpose);
    }

    private void validate(String category, String technology) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("기술 스택 분류는 필수입니다.");
        }
        if (technology == null || technology.isBlank()) {
            throw new IllegalArgumentException("기술명은 필수입니다.");
        }
    }
}
