# GEO 포트폴리오 + 블로그 — 백엔드 작성 가이드

김지오(GEO) 포트폴리오/블로그의 **API 서버**다. 프론트엔드(`../fe`, Next.js)가 소비하는 콘텐츠(프로젝트·기술 스택·경력/학력 등)를 제공하고, 사이트 소유자 1인이 그 콘텐츠를 관리(생성·수정·삭제)한다. 이 문서는 **아키텍처 컨벤션(유지)** 과 **도메인·API 재설계 방향(다시 만듦)** 을 정의한다.

## 스택 (실제 리포 기준)

- **Java 21** · **Spring Boot 3.5.3** · **Gradle**
- Spring Web · Spring Data JPA(Hibernate) · Bean Validation · Spring Security
- **QueryDSL 5.1.0 (jakarta)** — 복합/동적 조회
- Lombok
- **MySQL** (`mysql-connector-j`)
- 베이스 패키지 `com.example.be`, **feature-first(도메인별) 패키지 구조**
- QueryDSL Q타입은 빌드 시 `src/main/generated`에 생성된다(빌드 산출물). VCS에는 넣지 않는다 — 이미 커밋돼 있으면 정리한다.

---

## ⚠️ 지금 상태: 도메인·API 재설계 중

현재 `project` / `techstack` 도메인 구현은 **잘못 설계돼 있어 도메인 모델과 API를 다시 만든다.** 단, 아래 표처럼 **아키텍처 뼈대와 공통 인프라는 그대로 유지**한다. 기존 도메인 코드는 *정답이 아니라 참고용*으로만 본다.

| 유지 (KEEP — 이건 규칙이다) | 다시 만듦 (REBUILD) |
| --- | --- |
| 패키지 구조 · 레이어링 · 의존 방향 | 엔티티(도메인) 모델과 필드 |
| CQRS식 command/query 서비스 분리 | 애그리거트 경계 · 연관관계 설계 |
| DTO 3계층(Request → Result → Response) 흐름 | API 리소스 · 엔드포인트 · 응답 계약 |
| QueryDSL 커스텀 리포지토리 패턴 | 도메인별 조회/정렬/필터 요구 |
| 공통 예외 인프라 · 보안 · 설정/DB | (아래 "다시 설계할 것" 참고) |

새 코드는 반드시 아래 "유지할 뼈대" 규칙을 따른다. 규칙과 어긋난 기존 코드가 있으면, 재설계하며 규칙 쪽으로 맞춘다.

---

## 유지할 뼈대 (KEEP)

### 1. 패키지 구조 (feature-first)

도메인별로 수직 분할한다. 도메인 하나가 자기 `domain / repository / service / controller / dto / exception`를 모두 가진다.

```
com.example.be
├─ common
│  ├─ config        # QuerydslConfig 등 인프라 빈
│  ├─ security      # SecurityConfig, AdminProperties
│  └─ exception     # BusinessException, ErrorCode, ErrorResponse, GlobalExceptionHandler
└─ <domain>         # project, techstack, experience …
   ├─ domain        # Entity, 값 객체(@Embeddable), enum
   ├─ repository    # JpaRepository + ...Custom + ...CustomImpl
   ├─ service
   │  ├─ command    # 쓰기 (@Transactional)
   │  └─ query      # 읽기 (@Transactional(readOnly = true))
   ├─ controller
   ├─ dto
   │  ├─ request    # 입력 (Bean Validation)
   │  ├─ result     # 서비스 경계 밖 전달용
   │  └─ response   # API 응답 표면
   └─ exception     # 도메인 구체 예외 (BusinessException 상속)
```

### 2. 레이어링 & 의존 방향

`Controller → Service(command|query) → Repository → Domain`. 역방향 의존 금지. 계층 간에는 항상 DTO/도메인 객체로 넘기고, **Entity를 컨트롤러/직렬화 경계 밖으로 노출하지 않는다.**

### 3. CQRS식 command/query 분리

- **CommandService**: 쓰기. 클래스에 `@Transactional`. 도메인 정적 팩토리로 엔티티를 만들고 저장한 뒤 `Result`로 변환해 반환.
- **QueryService**: 읽기. 클래스에 `@Transactional(readOnly = true)`. 리포지토리에서 곧바로 `Result`를 받아 반환.
- 생성자 주입(`private final` + `@RequiredArgsConstructor`). 필드 주입 금지.

### 4. DTO 3계층: Request → Result → Response

| 계층 | 위치 | 책임 | 규칙 |
| --- | --- | --- | --- |
| **Request** | `dto/request` | API 입력 | `record`. Bean Validation(`@NotBlank`, `@Size`, `@NotNull`, `@Min/@Max`, 원소 검증 `List<@NotBlank String>`)으로 **형식**을 강제. `id`·소유자 등 클라이언트가 정해선 안 되는 필드는 넣지 않는다. |
| **Result** | `dto/result` | 서비스 경계 밖 전달 | `record`. Command가 방금 저장한 Entity(`from(entity)`)와 Query의 QueryDSL Projection이 **둘 다** 이걸 만든다. `Projections.constructor` 대상이면 **생성자 인자 순서 = record 컴포넌트 순서**를 반드시 지킨다. |
| **Response** | `dto/response` | API 응답 표면 | `record` + `from(result)`. 직렬화 포맷을 확정한다(예: `LocalDate` → `toString()` 문자열, enum → `name()` 문자열). 내부 표현을 그대로 흘리지 않는다. |

컨트롤러는 `Request`를 받아 서비스에 **개별 인자로 풀어** 넘기고, 서비스가 준 `Result`를 `Response.from(...)`으로 감싸 반환한다.

### 5. 도메인(Entity) 설계 규칙

- **애그리거트 루트** 중심. 자식은 루트를 통해서만 조작.
- `@NoArgsConstructor(access = PROTECTED)` + **private 전 인자 생성자** + **정적 팩토리 `create(...)`**. `new`로 직접 만들지 않는다.
- **불변식은 생성/변경 시점에 검증**한다(예: `DevelopmentPeriod`의 시작일 ≤ 종료일, `TechStack` 실력 점수 1~5). 불변식 위반은 `IllegalArgumentException`(→ 전역 핸들러가 400으로 변환).
- **setter 금지.** 상태 변경은 의미 있는 도메인 메서드로(`changeProficiency(...)` 처럼).
- 값 객체는 `@Embeddable`(예: 기간). 단일 스칼라 값의 목록은 `@ElementCollection`. 자체 속성(정렬 순서·플래그 등)을 갖는 자식은 별도 `@Entity` + `@OneToMany(cascade = ALL, orphanRemoval = true)`.
- `@Getter`는 Lombok. ID는 `GenerationType.IDENTITY`(MySQL).

### 6. QueryDSL 커스텀 리포지토리 패턴

- `XxxRepository extends JpaRepository<Xxx, Long>, XxxRepositoryCustom`.
- `XxxRepositoryCustom`(인터페이스) + `XxxRepositoryCustomImpl`(구현, `JPAQueryFactory` 주입, `@RequiredArgsConstructor`). 구현체 이름은 반드시 `...Impl`.
- **조회는 Entity가 아니라 `Result`로 직접 Projection**(`Projections.constructor(Result.class, ...)`)하는 것을 우선한다.
- **컬렉션 2개 이상 fetch join 금지** — 카테시안 곱 + Hibernate `MultipleBagFetchException`. 스칼라 본문은 한 번에 Projection하고, 각 컬렉션은 `id`로 좁힌 별도 쿼리로 조회해 조립한다(참고: `ProjectRepositoryCustomImpl#findResultById`).
- **정렬은 화이트리스트**로. `Map<String, ComparableExpressionBase<?>>`에 등록된 프로퍼티만 반영하고 나머지는 무시(미검증 컬럼 정렬·인젝션 차단). 유효 정렬이 없으면 **안정적 기본 정렬**로 폴백(참고: `toOrderSpecifiers`).
- 페이징은 `Pageable` + `PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne)`(카운트 쿼리 분리).

### 7. 공통 예외 인프라

- 도메인 예외는 `common.exception.BusinessException`(적절한 `ErrorCode` 전달)을 **상속**한다(예: `ProjectNotFoundException` → `RESOURCE_NOT_FOUND`).
- `ErrorCode`(enum)에는 **HTTP 계층에서 통하는 일반 코드만** 둔다(도메인 개념을 넣지 않는다).
- `GlobalExceptionHandler`(`@RestControllerAdvice`)가 `BusinessException` / `MethodArgumentNotValidException` / `IllegalArgumentException`을 **정제된 `ErrorResponse`(code, message)** 로 변환한다. **스택 트레이스·SQL·클래스명·서버 경로 등 내부 정보를 응답에 절대 노출하지 않는다.**

### 8. 보안 (deny-by-default)

- `GET /api/**`는 **공개**(콘텐츠 조회). 그 외 모든 요청(쓰기)은 **`ROLE_ADMIN` 인증 필요**.
- **STATELESS** + **HTTP Basic**, CSRF 비활성(상태 없는 REST라).
- 관리자 계정은 `InMemoryUserDetailsManager` 1인. 자격 증명은 **`AdminProperties`(`app.admin.*`)로 환경 변수 주입**, 비밀번호는 **BCrypt 해시**. **시크릿을 소스에 하드코딩 금지.**
- 리소스 단위 권한 판단이 필요하면 Service 계층에서. SecurityConfig는 인증 여부 + 역할 기반 최소 접근만.

### 9. 설정 / DB

- `application.yaml`은 전부 **환경 변수 주입**(`${DB_HOST}`, `${ADMIN_PASSWORD_HASH}` 등). 로컬 기본값은 개발 편의용.
- `spring.jpa.open-in-view: false` 유지(뷰 계층 지연 로딩 금지 — 조회는 Projection으로 끝낸다).
- `ddl-auto`는 `${JPA_DDL_AUTO:update}`(개발). 운영 스키마는 별도 관리.
- 타임존 `Asia/Seoul`. **DB는 MySQL 유지.**

---

## 다시 설계할 것 (REBUILD — 도메인·API)

### 설계 기준 = FE가 실제로 필요로 하는 데이터

**도메인 모델과 응답 계약의 소스 오브 트루스는 `../fe`(프론트) 화면이 요구하는 데이터다.** `../fe/CLAUDE.md`의 페이지 구조와 실제 페이지(`src/app/*`)가 "무엇을 보여주는지"를 먼저 읽고, 그걸 만족하도록 엔티티/응답을 설계한다. 기존 필드에 억지로 끼워 맞추지 않는다.

### 관리 도메인 범위

**Project · TechStack · Experience(경력/학력)** 세 도메인.

#### Project (프로젝트 목록 + 케이스스터디 상세)

FE 요구:
- **목록 카드**(`/projects`, 카테고리별 그룹): 썸네일 · 태그(기술) · 제목 · 한 줄 소개 · 날짜.
- **상세 케이스스터디**(`/projects/[slug]`): 브레드크럼 → 제목 → 메타 그리드(**기간 / 팀 / 역할 / GitHub**) → 히어로 이미지 → 좌측 TOC + 아티클. 섹션: **Overview → 아키텍처(해결 방법) → 결론(지표 카드) → Troubleshooting → 배운 점 → Tech Stack(표)**.

현재 엔티티 대비 **재설계 시 검토할 갭**: 라우팅용 `slug`, 목록 그룹핑용 `category`, 메타의 `role`·`githubUrl`, 결론의 **지표(before→after) 카드 데이터(metrics)**, **배운 점(learnings)** 이 현재 모델에 없다. `contribution`(해결 방법/맡은 부분)·`conclusion`·`troubleshooting`·`images`(대표 + 부가) 개념은 유지 후보.

#### TechStack (기술 역량)

FE 요구: profile/experience의 **Technical Expertise** — 카테고리별로 묶어 표시(Backend / DevOps / …), 스킬마다 부가 설명(note) 노출. 현재 `category(enum) · name · imageUrl · proficiency(1~5)` 를 갖는다. FE가 카테고리 그룹 + 스킬별 설명을 쓰므로 **카테고리 enum 값과 note/description 필드**를 FE와 맞춘다.

#### Experience (경력 / 학력 타임라인) — 신규

FE 요구(`/experience`, 방금 구현): **Career**와 **Education** 두 그룹의 세로 타임라인. 각 항목 = **기간 · 제목 · 소속(org) · 불릿 설명(복수) · 대표 강조 여부**. 설계 힌트:
- `type` enum: `CAREER` / `EDUCATION`.
- 기간: 시작~종료(진행 중/졸업처럼 단일 시점만 있는 경우 허용). FE는 `2026.01 — 2026.12`, `2019.02` 같은 표기를 쓴다 — 저장은 날짜로, 표기는 응답/프론트에서 포맷.
- 불릿 설명은 `@ElementCollection`(순서 보존), `highlighted` 플래그, 목록 정렬용 `sortOrder`.

### API 컨벤션

- 베이스 `/api`, 리소스는 **복수형 kebab-case**(`/api/projects`, `/api/tech-stacks`, `/api/experiences`).
- **읽기(GET)는 공개, 쓰기(POST/PUT/PATCH/DELETE)는 ADMIN**(보안 규칙과 일치).
- 목록: 큰 컬렉션(projects)은 `Pageable`(`@PageableDefault`) 페이징 + `Page<...Response>`. 소규모 고정 목록(tech-stacks/experiences)은 정렬된 `List<...Response>` 전체 반환 가능.
- 생성은 `201 CREATED` + 생성 리소스 `Response`, 단건 조회는 `200`, 없는 리소스는 도메인 예외 → `404`.
- **CRUD 완성**: 현재는 `create` + 조회만 있다. 소유자 관리(수정/삭제)가 목적이므로 재설계 시 **update(PUT/PATCH) · delete(DELETE)** 를 포함해 설계한다(수정 요청 DTO도 request 계층에 추가).

---

## 코딩 컨벤션

- DTO는 **Java `record`**, Entity/Service/RepositoryImpl은 Lombok(`@Getter`, `@RequiredArgsConstructor`, `@NoArgsConstructor(PROTECTED)`).
- **검증 위치 이원화**: 형식·필수·범위 = Request(Bean Validation) / 도메인 불변식 = Entity 생성·변경 시점. 같은 규칙을 양쪽에 중복 구현하기보다, 표면 검증(빠른 400)과 도메인 최종 방어를 각자 책임지게 한다.
- 주석·예외 메시지·문서는 **한국어**(리포 전체 톤 유지). 메시지는 사용자에게 노출돼도 안전한 표현으로.
- 새 도메인은 위 "패키지 구조/레이어링/DTO 3계층/QueryDSL 패턴"을 **그대로 복제**해 시작한다(`project`·`techstack`가 레퍼런스 형태 — 단, 필드/모델은 재설계).
- 테스트는 JUnit 5(`spring-boot-starter-test`). 최소한 도메인 불변식과 QueryDSL 조회(Projection·정렬·페이징)를 커버.

## 실행 / 빌드

- 빌드: `./gradlew build` (QueryDSL Q타입이 `src/main/generated`에 먼저 생성돼야 컴파일된다).
- 실행: `./gradlew bootRun`.
- 필요한 환경 변수: `DB_HOST` · `DB_PORT` · `DB_NAME` · `DB_USERNAME` · `DB_PASSWORD` · `ADMIN_USERNAME` · `ADMIN_PASSWORD_HASH`(BCrypt 해시) · (선택) `JPA_DDL_AUTO`.
- Q타입이 꼬이면 `./gradlew clean`(생성 디렉토리 삭제) 후 재빌드.
