package com.example.be.project.dto.result;

import com.example.be.project.domain.Metric;

/**
 * 결론 지표(before → after) 결과. {@link com.example.be.project.domain.Project}의 metrics를
 * 서비스 경계 밖으로 전달하기 위한 변환 결과다.
 */
public record MetricResult(
        String label,
        String before,
        String after,
        String description
) {
    public static MetricResult from(Metric metric) {
        return new MetricResult(
                metric.getLabel(),
                metric.getBefore(),
                metric.getAfter(),
                metric.getDescription()
        );
    }
}
