package com.example.be.common.exception;

/**
 * 클라이언트에 반환하는 정제된 오류 응답. 내부 구현(스택 트레이스, SQL, 클래스명, 서버 경로)을 담지 않는다.
 */
public record ErrorResponse(
        String code,
        String message
) {
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.name(), message);
    }
}
