package com.example.be.techpart.service.command;

/**
 * 스킬 입력을 서비스 경계로 넘기기 위한 커맨드. 컨트롤러가 Request(DTO)에서 풀어 전달하고,
 * Command Service가 도메인 값 객체({@code TechSkill})로 변환한다.
 */
public record TechSkillCommand(String name, String note) {
}
