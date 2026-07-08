package com.example.be.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 사이트 소유자(관리자) 자격 증명. 시크릿을 소스에 하드코딩하지 않고 외부 설정(환경 변수)으로 주입한다.
 *
 * <p>{@code passwordHash}는 평문이 아닌 BCrypt 단방향 해시 값이어야 한다.
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        String username,
        String passwordHash
) {
}
