package com.example.be.project.service.command;

/**
 * Tech Stack 표 행 입력의 서비스 계층 전달 객체. 컨트롤러가 요청 DTO(ProjectTechRequest)를 이 타입으로
 * 풀어 서비스에 넘기고, Command Service가 도메인 값 객체({@link com.example.be.project.domain.ProjectTech})로
 * 변환한다.
 */
public record ProjectTechCommand(
        String category,
        String technology,
        String purpose
) {
}
