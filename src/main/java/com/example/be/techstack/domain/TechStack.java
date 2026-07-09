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
 * 보유 기술 스택. 분류(카테고리)별로 묶어 보여주며, 스킬마다 부가 설명(note)을 노출한다.
 *
 * <p>이미지는 선택이다(프론트는 카테고리 아이콘을 쓰고 스킬 이미지는 필수로 요구하지 않는다).
 * 같은 카테고리 안에서의 노출 순서는 {@code sortOrder}로 관리한다.
 */
@Entity
@Table(name = "tech_stack")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private TechStackCategory category;

    /** 스킬별 부가 설명. 이력 화면에서 스킬명 아래에 노출한다. */
    @Column(name = "note", nullable = false, length = 500)
    private String note;

    /** 스킬 이미지 URL. 선택 항목이다. */
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    /** 같은 카테고리 안에서의 노출 순서. 값이 작을수록 먼저 노출한다. */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    private TechStack(String name, TechStackCategory category, String note, String imageUrl, int sortOrder) {
        validateName(name);
        validateCategory(category);
        validateNote(note);
        this.name = name;
        this.category = category;
        this.note = note;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public static TechStack create(String name, TechStackCategory category, String note, String imageUrl, int sortOrder) {
        return new TechStack(name, category, note, imageUrl, sortOrder);
    }

    /**
     * 항목 전체를 새 값으로 교체한다(PUT 의미). 불변식은 변경 시점에 다시 검증한다.
     */
    public void update(String name, TechStackCategory category, String note, String imageUrl, int sortOrder) {
        validateName(name);
        validateCategory(category);
        validateNote(note);
        this.name = name;
        this.category = category;
        this.note = note;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
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

    private void validateNote(String note) {
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("기술 스택 설명은 필수입니다.");
        }
    }
}
