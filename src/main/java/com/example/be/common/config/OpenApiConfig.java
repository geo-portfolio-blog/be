package com.example.be.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI(Swagger) 문서 설정. API 기본 정보와 관리자 인증 스킴을 정의한다.
 *
 * <p>조회(GET)는 공개라 인증 없이 호출되지만, 생성 등 쓰기 API는 HTTP Basic(ADMIN)이 필요하다.
 * Swagger UI 우상단 "Authorize"에 관리자 자격 증명을 입력하면 쓰기 API도 문서에서 바로 호출해볼 수 있다.
 */
@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH = "basicAuth";

    @Bean
    public OpenAPI portfolioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GEO 포트폴리오 API")
                        .description("김지오 포트폴리오/블로그의 콘텐츠 제공 및 관리 API. "
                                + "조회(GET)는 공개, 쓰기는 관리자(ADMIN) HTTP Basic 인증이 필요하다.")
                        .version("v0.0.1"))
                .schemaRequirement(BASIC_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic"));
    }
}
