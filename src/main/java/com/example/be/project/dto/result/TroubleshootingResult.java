package com.example.be.project.dto.result;

import java.util.List;

/**
 * Troubleshooting 결과. "상황 & 원인"과 "해결 & 결과" 불릿을 묶는다.
 * 둘 다 비어 있으면 {@link #from} 은 null을 반환해 상세 결과에서 통째로 생략된다.
 */
public record TroubleshootingResult(
        String situationCause,
        List<String> solutions
) {
    public static TroubleshootingResult from(String situationCause, List<String> solutions) {
        boolean noSituation = situationCause == null || situationCause.isBlank();
        boolean noSolutions = solutions == null || solutions.isEmpty();
        if (noSituation && noSolutions) {
            return null;
        }
        return new TroubleshootingResult(
                situationCause,
                solutions == null ? List.of() : List.copyOf(solutions)
        );
    }
}
