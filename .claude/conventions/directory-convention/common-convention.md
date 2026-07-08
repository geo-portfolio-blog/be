# Common 패키지 컨벤션

## 허용 구조

```text
common
├─ config
├─ exception
├─ security
├─ logging
├─ external
└─ scheduler
```

필요한 패키지만 생성한다.

## MUST

- `common`에는 특정 도메인에 속하지 않고 둘 이상의 도메인에서 재사용되는 코드 또는 전역 인프라만 둔다.
- `common`은 특정 도메인 패키지를 import하지 않는다.
- 허용 목록 외 하위 패키지를 임의로 만들지 않는다.
- `util`, `helper`, `manager`, `misc`, `shared`처럼 책임이 불명확한 패키지를 만들지 않는다.
- 특정 도메인만 사용하는 코드를 재사용 가능성만으로 `common`에 올리지 않는다.
- 모든 Spring Bean 설정 클래스는 `common.config`에 둔다.
- 특정 도메인에만 적용되는 설정은 `ReservationQueryDslConfig`처럼 클래스명으로 대상을 드러낸다.

## 패키지 책임

### config

- QueryDSL, Jackson, CORS, OpenAPI 및 공통 Bean 설정
- 특정 도메인 설정도 패키지를 새로 만들지 않고 명확한 클래스명으로 구분

### exception

- `GlobalExceptionHandler`
- 공통 `BusinessException`, `ErrorCode`, `ErrorResponse`
- 도메인별 구체 예외는 각 도메인의 `exception`에 배치

### security

- 인증·인가 필터, 토큰 처리, Security 설정
- 특정 도메인의 비즈니스 권한 판단 로직을 두지 않음

### logging

- 전역 로깅 필터, MDC, 공통 로그 정책
- 단순 문자열 처리 유틸을 모아두는 장소로 사용하지 않음

### external

- 외부 API 공통 클라이언트 설정과 외부 시스템 연동 컴포넌트
- 외부 시스템별 새 하위 패키지가 필요하면 사용자 승인을 받음
- 도메인 유스케이스는 Service에 유지

### scheduler

- 전역 스케줄 설정과 스케줄 실행 진입점
- 스케줄러는 복잡한 비즈니스 로직을 직접 구현하지 않고 적절한 Service를 호출

## 새 공용 코드 판단 기준

다음을 모두 만족할 때만 `common` 배치를 고려한다.

1. 특정 도메인 이름 없이 설명할 수 있다.
2. 둘 이상의 도메인에서 실제로 사용한다.
3. 공통으로 이동해도 특정 도메인을 import하지 않는다.
4. 책임을 `config`, `exception`, `security`, `logging`, `external`, `scheduler` 중 하나로 명확히 분류할 수 있다.

하나라도 만족하지 않으면 해당 도메인에 둔다.
