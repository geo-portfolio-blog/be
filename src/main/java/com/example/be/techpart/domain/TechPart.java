package com.example.be.techpart.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Technical Expertise의 분류(Part) 애그리거트 루트.
 *
 * <p>이력 화면의 "Backend / DevOps / Others" 같은 분류를 1급 개념으로 두고, 각 Part가 스킬 목록을
 * 소유한다. 스킬은 이름·설명 두 스칼라 속성만 갖는 값 객체({@link TechSkill})이므로 순서가 의미를 갖는
 * {@link ElementCollection} + {@link OrderColumn}으로 보존한다. Part 자체의 노출 순서는
 * {@code sortOrder}로 관리한다. 스킬은 루트(Part)를 통해서만 교체한다(setter 없음).
 */
@Entity
@Table(name = "tech_part")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 분류 표시명(예: "Backend"). 소유자가 자유롭게 정한다. */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 분류 노출 순서. 값이 작을수록 먼저 노출한다. */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @ElementCollection
    @CollectionTable(name = "tech_part_skill", joinColumns = @JoinColumn(name = "tech_part_id"))
    @OrderColumn(name = "skill_order")
    private List<TechSkill> skills = new ArrayList<>();

    private TechPart(String name, int sortOrder) {
        validateName(name);
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public static TechPart create(String name, int sortOrder, List<TechSkill> skills) {
        TechPart part = new TechPart(name, sortOrder);
        part.replaceSkills(skills);
        return part;
    }

    /**
     * 분류 전체를 새 값으로 교체한다(PUT 의미). 불변식은 변경 시점에 다시 검증한다.
     */
    public void update(String name, int sortOrder, List<TechSkill> skills) {
        validateName(name);
        this.name = name;
        this.sortOrder = sortOrder;
        replaceSkills(skills);
    }

    private void replaceSkills(List<TechSkill> skills) {
        this.skills.clear();
        if (skills != null) {
            for (TechSkill skill : skills) {
                if (skill != null) {
                    this.skills.add(skill);
                }
            }
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("분류명은 필수입니다.");
        }
    }
}
