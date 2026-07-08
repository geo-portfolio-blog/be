package com.example.be.techstack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 보유 기술 스택. 기술 스택 사진, 분류, 이름, 실력 점수(1~5)를 가진다.
 *
 * <p>실력 점수는 1~5 범위라는 도메인 불변식을 가지므로 생성·변경 시점에 검증한다.
 */
@Entity
@Table(name = "tech_stack")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechStack {

    private static final int MIN_PROFICIENCY = 1;
    private static final int MAX_PROFICIENCY = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private TechStackCategory category;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "proficiency", nullable = false)
    private int proficiency;

    private TechStack(String name, TechStackCategory category, String imageUrl, int proficiency) {
        validateName(name);
        validateCategory(category);
        validateImageUrl(imageUrl);
        validateProficiency(proficiency);
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.proficiency = proficiency;
    }

    public static TechStack create(String name, TechStackCategory category, String imageUrl, int proficiency) {
        return new TechStack(name, category, imageUrl, proficiency);
    }

    public void changeProficiency(int proficiency) {
        validateProficiency(proficiency);
        this.proficiency = proficiency;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("기술 스택명은 필수입니다.");
        }
    }

    private void validateCategory(TechStackCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("기술 스택 분류는 필수입니다.");
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("기술 스택 이미지 URL은 필수입니다.");
        }
    }

    private void validateProficiency(int proficiency) {
        if (proficiency < MIN_PROFICIENCY || proficiency > MAX_PROFICIENCY) {
            throw new IllegalArgumentException("실력 점수는 1점 이상 5점 이하여야 합니다.");
        }
    }
}
