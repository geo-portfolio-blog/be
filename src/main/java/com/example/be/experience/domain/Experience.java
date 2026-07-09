package com.example.be.experience.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * 경력/학력 타임라인 항목 애그리거트 루트.
 *
 * <p>불릿 설명은 단일 스칼라 값의 목록이고 노출 순서가 의미를 가지므로 {@link ElementCollection} +
 * {@link OrderColumn}으로 순서를 보존한다. 항목 자체의 타임라인 노출 순서는 {@code sortOrder}로 관리한다.
 */
@Entity
@Table(name = "experience")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ExperienceType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** 소속. 고등학교 졸업처럼 소속이 없는 항목은 비운다. */
    @Column(name = "organization", length = 200)
    private String organization;

    @Embedded
    private ExperiencePeriod period;

    @ElementCollection
    @CollectionTable(name = "experience_point", joinColumns = @JoinColumn(name = "experience_id"))
    @OrderColumn(name = "point_order")
    @Column(name = "content", nullable = false, length = 1000)
    private List<String> points = new ArrayList<>();

    /** true면 타임라인에서 대표 항목으로 강조(primary 도트)한다. */
    @Column(name = "highlighted", nullable = false)
    private boolean highlighted;

    /** 같은 그룹 안에서의 노출 순서. 값이 작을수록 먼저 노출한다. */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    private Experience(
            ExperienceType type,
            String title,
            String organization,
            ExperiencePeriod period,
            boolean highlighted,
            int sortOrder
    ) {
        validateType(type);
        validateTitle(title);
        validatePeriod(period);
        this.type = type;
        this.title = title;
        this.organization = organization;
        this.period = period;
        this.highlighted = highlighted;
        this.sortOrder = sortOrder;
    }

    public static Experience create(
            ExperienceType type,
            String title,
            String organization,
            ExperiencePeriod period,
            List<String> points,
            boolean highlighted,
            int sortOrder
    ) {
        Experience experience = new Experience(type, title, organization, period, highlighted, sortOrder);
        experience.replacePoints(points);
        return experience;
    }

    /**
     * 항목 전체를 새 값으로 교체한다(PUT 의미). 불변식은 변경 시점에 다시 검증한다.
     */
    public void update(
            ExperienceType type,
            String title,
            String organization,
            ExperiencePeriod period,
            List<String> points,
            boolean highlighted,
            int sortOrder
    ) {
        validateType(type);
        validateTitle(title);
        validatePeriod(period);
        this.type = type;
        this.title = title;
        this.organization = organization;
        this.period = period;
        this.highlighted = highlighted;
        this.sortOrder = sortOrder;
        replacePoints(points);
    }

    private void replacePoints(List<String> points) {
        this.points.clear();
        if (points != null) {
            for (String point : points) {
                if (point != null && !point.isBlank()) {
                    this.points.add(point);
                }
            }
        }
    }

    private void validateType(ExperienceType type) {
        if (type == null) {
            throw new IllegalArgumentException("경험 유형은 필수입니다.");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
    }

    private void validatePeriod(ExperiencePeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("활동 기간은 필수입니다.");
        }
    }
}
