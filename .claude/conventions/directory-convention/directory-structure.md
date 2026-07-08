# 디렉터리 구조 컨벤션

## 최상위 구조

```text
com.example.be
├─ common
│  ├─ config
│  ├─ exception
│  ├─ security
│  ├─ logging
│  ├─ external
│  └─ scheduler
├─ {domain}
│  ├─ controller
│  ├─ service
│  │  ├─ command
│  │  └─ query
│  ├─ dto
│  │  ├─ request
│  │  ├─ result
│  │  └─ response
│  ├─ domain
│  ├─ repository
│  └─ exception
└─ Application.java
```

## MUST

> 위 구조는 생성 가능한 패키지의 허용 목록이다. 모든 패키지를 미리 만들라는 의미가 아니다.
> 실제 코드가 필요한 패키지만 생성하고 빈 패키지를 만들지 않는다.
> 허용 목록 외 패키지가 필요하면 임의로 추가하지 않고 사용자에게 필요성과 대안을 보고한다.

- 패키지는 계층 우선이 아닌 도메인·기능 단위로 나눈다.
- 최상위에는 `common`, 도메인 패키지, `Application.java`만 둔다.
- 각 도메인에서 생성 가능한 직접 하위 패키지는 `controller`, `service`, `dto`, `domain`, `repository`, `exception`으로 제한한다.
- `service` 하위에는 필요한 경우 `command`, `query`만 생성한다.
- `dto` 하위에는 필요한 경우 `request`, `result`, `response`만 생성한다.
- `repository` 하위에 별도 패키지를 만들지 않는다.
- `mapper`, `facade`, `util`, `helper`, `manager`, `dao`, `client`, `adapter`, `usecase`, `application` 패키지를 도메인 아래에 임의로 만들지 않는다.
- 모든 Spring 설정 클래스는 `common.config`에 둔다. 특정 도메인에만 적용되는 설정은 클래스명으로 대상을 드러낸다.
- 특정 도메인 예외는 `{domain}.exception`, 여러 도메인에서 공유되는 예외 인프라는 `common.exception`에 둔다.

## 패키지별 책임

| 패키지 | 책임 |
|---|---|
| `controller` | HTTP 입력 검증, Service 호출, Result → Response 변환 |
| `service.command` | 생성·수정·삭제 유스케이스와 쓰기 트랜잭션 |
| `service.query` | 조회 유스케이스와 읽기 전용 트랜잭션 |
| `dto.request` | 클라이언트 입력 및 검색 조건 |
| `dto.result` | Service 반환 값 |
| `dto.response` | API 응답 표현 |
| `domain` | Entity, Value Object, Enum 및 도메인 행위 |
| `repository` | Spring Data JPA와 QueryDSL 데이터 접근 |
| `exception` | 해당 도메인의 구체적인 비즈니스 예외와 오류 코드 |

## 허용 의존 방향

```text
Controller → Service → Repository
                    → Domain
Controller → Request / Result / Response
Service    → Request / Result / Domain / Repository / Exception
Repository → Domain / Result / Request(검색 조건만)
```

## 금지 의존 방향

```text
Controller → Repository
Domain     → Controller
Domain     → Service
Domain     → Request / Result / Response
Repository → Controller
Repository → Service
Common     → 특정 도메인
```

`common`은 특정 도메인을 import하지 않는다. 특정 도메인에 종속되는 코드는 해당 도메인에 둔다.

## 기존 코드와 충돌할 때

- 기존 구조가 이 문서와 다르면 새 코드에서 조용히 기존 구조를 확장하지 않는다.
- 충돌 파일과 예상 영향을 보고한다.
- 사용자의 명시적 승인 없이 새 레이어 또는 패키지를 도입하지 않는다.

## 작업 종류별 참조 문서

| 작업 | 문서 |
|---|---|
| Controller/API | `./controller-convention.md` |
| Service | `./service-convention.md` |
| DTO | `./dto-convention.md` |
| Entity/Domain | `./domain-convention.md` |
| Repository/JPA/QueryDSL | `./repository-convention.md` |
| 예외 | `./exception-convention.md` |
| Common | `./common-convention.md` |
| 테스트 | `./test-convention.md` |
