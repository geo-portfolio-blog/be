package com.example.be.experience.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 경험 활동 기간 값 객체. 시작일은 필수이고, 종료일은 선택이다.
 *
 * <p>졸업처럼 단일 시점만 있는 항목은 종료일을 비운다({@code endDate == null}).
 * 종료일이 있으면 시작일 이후여야 한다는 불변식(시작일 &le; 종료일)을 생성 시점에 검증한다.
 * 표기(예: {@code 2026.01 — 2026.12}, {@code 2019.02})는 응답/프론트에서 포맷하고, 저장은 날짜로 한다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExperiencePeriod {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private ExperiencePeriod(LocalDate startDate, LocalDate endDate) {
        validate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static ExperiencePeriod of(LocalDate startDate, LocalDate endDate) {
        return new ExperiencePeriod(startDate, endDate);
    }

    private void validate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("활동 기간의 시작일은 필수입니다.");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("활동 기간의 종료일은 시작일보다 앞설 수 없습니다.");
        }
    }
}
