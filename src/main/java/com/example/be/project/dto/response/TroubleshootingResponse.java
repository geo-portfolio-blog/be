package com.example.be.project.dto.response;

import com.example.be.project.dto.result.TroubleshootingResult;
import java.util.List;

/**
 * Troubleshooting API 응답. "상황 & 원인"과 "해결 & 결과" 불릿을 담는다.
 */
public record TroubleshootingResponse(
        String situationCause,
        List<String> solutions
) {
    public static TroubleshootingResponse from(TroubleshootingResult result) {
        return new TroubleshootingResponse(
                result.situationCause(),
                result.solutions()
        );
    }
}
