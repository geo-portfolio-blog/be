package com.example.be.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Troubleshooting 입력. "상황 & 원인" 본문(situationCause)과 "해결 & 결과" 불릿(solutions)으로 구성한다.
 * 전체가 없을 수 있으므로 프로젝트 요청에서 이 객체 자체가 null일 수 있다.
 */
public record TroubleshootingRequest(
        @Size(max = 10000)
        String situationCause,

        @Size(max = 30)
        List<@NotBlank @Size(max = 1000) String> solutions
) {
}
