package com.example.be.common.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 소유자 1인 포트폴리오용 인증·인가 설정.
 *
 * <p>deny-by-default 정책을 적용한다. 콘텐츠 조회(GET)는 공개 엔드포인트로 명시적으로 예외 처리하고,
 * 그 외 모든 요청(생성·수정·삭제 등 쓰기)은 관리자(ADMIN) 인증을 요구한다.
 * 인증되지 않은 쓰기 요청은 이 단계에서 401로 차단된다.
 *
 * <p>리소스 단위 소유권/권한 판단은 Service 계층에서 수행하며, 여기서는 인증 여부와
 * 역할 기반의 최소 접근 제어만 담당한다.
 */
@Configuration
@EnableConfigurationProperties(AdminProperties.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 상태를 갖지 않는 REST API + HTTP Basic 조합이므로 CSRF 세션 토큰을 사용하지 않는다.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // API 문서(OpenAPI 명세 + Swagger UI)는 공개.
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 포트폴리오 콘텐츠 조회는 공개.
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        // 그 외(쓰기 포함) 모든 요청은 관리자 인증 필요 (deny-by-default).
                        .anyRequest().hasRole("ADMIN"))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AdminProperties adminProperties) {
        UserDetails admin = User.withUsername(adminProperties.username())
                .password(adminProperties.passwordHash())
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
