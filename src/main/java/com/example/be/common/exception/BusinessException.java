package com.example.be.common.exception;

/**
 * 비즈니스 규칙 위반을 표현하는 공통 예외의 기반 타입. 도메인별 구체 예외가 이를 상속하고
 * 적절한 {@link ErrorCode}를 전달한다. {@link GlobalExceptionHandler}가 정제된 오류 응답으로 변환한다.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
