package com.example.be.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 결론의 지표(before → after) 값 객체.
 *
 * <p>케이스스터디 상세의 "결론" 섹션에 지표 카드로 노출한다. 지표명(label)과 개선 전/후 값은 필수이고,
 * 부가 설명(description)은 선택이다. {@code before}/{@code after}는 "60%", "100ms"처럼 단위를 포함한
 * 표시 문자열로 저장한다(수치 연산 대상이 아니므로 문자열로 둔다).
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Metric {

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "metric_before", nullable = false, length = 200)
    private String before;

    @Column(name = "metric_after", nullable = false, length = 200)
    private String after;

    @Column(name = "description", length = 500)
    private String description;

    private Metric(String label, String before, String after, String description) {
        validate(label, before, after);
        this.label = label;
        this.before = before;
        this.after = after;
        this.description = description;
    }

    public static Metric of(String label, String before, String after, String description) {
        return new Metric(label, before, after, description);
    }

    private void validate(String label, String before, String after) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("지표명은 필수입니다.");
        }
        if (before == null || before.isBlank()) {
            throw new IllegalArgumentException("지표의 개선 전 값은 필수입니다.");
        }
        if (after == null || after.isBlank()) {
            throw new IllegalArgumentException("지표의 개선 후 값은 필수입니다.");
        }
    }
}
