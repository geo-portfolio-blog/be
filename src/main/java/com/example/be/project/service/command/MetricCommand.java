package com.example.be.project.service.command;

/**
 * 지표 입력의 서비스 계층 전달 객체. 컨트롤러가 요청 DTO(MetricRequest)를 이 타입으로 풀어 서비스에 넘기고,
 * Command Service가 이를 도메인 값 객체({@link com.example.be.project.domain.Metric})로 변환한다.
 * 요청 DTO를 서비스로, 도메인 객체를 컨트롤러로 흘리지 않기 위한 경계 타입이다.
 */
public record MetricCommand(
        String label,
        String before,
        String after,
        String description
) {
}
