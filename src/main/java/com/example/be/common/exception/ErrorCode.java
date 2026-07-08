package com.example.be.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 전역에서 공유하는 오류 코드. 특정 도메인 개념을 담지 않고 HTTP 계층에서 의미가 통하는
 * 일반 코드만 정의한다. 도메인별 구체 예외는 각 도메인 {@code exception} 패키지에서
 * 적절한 코드를 선택해 {@link BusinessException}을 상속한다.
 */
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청을 처리하지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
