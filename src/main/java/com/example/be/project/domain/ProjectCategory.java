package com.example.be.project.domain;

/**
 * 프로젝트 분류. 목록 화면 상단 필터(Backend / DevOps / AI / 기타)에 대응하는 고정 카테고리다.
 * ("전체"는 필터 UI일 뿐 저장 값이 아니다.)
 */
public enum ProjectCategory {

    BACKEND,
    DEVOPS,
    AI,
    ETC
}
