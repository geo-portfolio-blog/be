package com.example.be.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개발 기간 값 객체. 시작일과 종료일을 하나의 불변식(시작일 <= 종료일)으로 묶는다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevelopmentPeriod {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    private DevelopmentPeriod(LocalDate startDate, LocalDate endDate) {
        validate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static DevelopmentPeriod of(LocalDate startDate, LocalDate endDate) {
        return new DevelopmentPeriod(startDate, endDate);
    }

    private void validate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("개발 기간의 시작일과 종료일은 필수입니다.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("개발 기간의 종료일은 시작일보다 앞설 수 없습니다.");
        }
    }
}
