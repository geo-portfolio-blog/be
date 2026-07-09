package com.example.be.project.dto.response;

import com.example.be.project.dto.result.MetricResult;

/**
 * 결론 지표(before → after) API 응답.
 */
public record MetricResponse(
        String label,
        String before,
        String after,
        String description
) {
    public static MetricResponse from(MetricResult result) {
        return new MetricResponse(
                result.label(),
                result.before(),
                result.after(),
                result.description()
        );
    }
}
