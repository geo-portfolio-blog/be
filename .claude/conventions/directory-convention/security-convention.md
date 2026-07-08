# Security 컨벤션

Spring 백엔드 코드가 지켜야 할 보안 규칙이다. 이 문서는 `spring-security-reviewer`의 판정 정본이며,
OWASP API Security Top 10과 OWASP REST Security Cheat Sheet를 이 프로젝트 구조(계층형·CQRS)에 맞게
구체화한 것이다.

## MUST

- 클라이언트 입력은 Entity에 직접 바인딩하지 않는다. 항상 `dto.request`의 Request DTO로 받는다.
- Request DTO에는 클라이언트가 제어해선 안 되는 필드(`id`, `role`, `status`, `createdBy`, 소유자 식별자 등)를
  포함하지 않는다. (Mass Assignment 방지)
- 요청 본문은 `@Valid`와 Bean Validation 제약(`@NotNull`, `@Size`, `@Pattern`, `@Email` 등)으로 검증한다.
  길이·범위·형식이 제한 가능한 필드에 제약 없이 `String`만 두지 않는다.
- 식별자로 리소스에 접근하는 모든 조회·수정·삭제는 **소유권 또는 권한 검증을 Service에서 수행**한다.
  URL의 ID가 유효하다는 것만으로 접근을 허용하지 않는다. (IDOR/BOLA 방지)
- 쿼리는 파라미터 바인딩을 사용한다. JPQL/QueryDSL에 사용자 입력을 문자열 연결로 넣지 않는다.
  네이티브 쿼리의 동적 문자열 조립을 금지한다. (인젝션 방지)
- 정렬·필드명처럼 파라미터 바인딩이 불가능한 동적 값은 허용 목록(화이트리스트)으로 검증한 뒤 사용한다.
- 비밀번호는 단방향 해시(BCrypt 등)로만 저장한다. 평문·양방향 암호화 저장을 금지한다.
- 시크릿(API 키, DB 비밀번호, 토큰 서명 키)을 소스 코드·설정 파일에 하드코딩하지 않는다.
  환경 변수 또는 외부 설정으로 주입한다.
- 민감정보(비밀번호, 토큰, 주민번호, 카드번호)를 로그에 남기지 않는다. Request/Entity 전체를
  `toString()`으로 로깅하지 않는다.
- 민감정보를 Response DTO에 포함하지 않는다. Entity를 API로 직접 노출하지 않는다
  (`dto-convention.md`의 Response 변환 규칙과 동일).
- 예외 메시지에 내부 구현(스택 트레이스, SQL, 클래스명, 서버 경로)을 담아 클라이언트에 반환하지 않는다.
  비즈니스 예외는 전역 예외 처리기에서 정제된 오류 응답으로 변환한다.
- 존재 여부를 노출하면 안 되는 리소스는 권한 없음과 존재하지 않음을 같은 응답(404)으로 처리하는 것을 고려한다.
- 인증이 필요한 엔드포인트를 기본으로 하고, 공개 엔드포인트를 예외적으로 명시한다.
  (deny-by-default) Security 설정은 `common.security`에 둔다.
- 권한 판단(인가)은 Controller가 아닌 Service 계층에서 수행한다. Controller에는 인증 주체 추출까지만 둔다.

## 예시

```java
// Service에서 소유권 검증 (IDOR 방지)
@Transactional
public ReservationResult cancel(Long memberId, Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));

    if (!reservation.isOwnedBy(memberId)) {
        throw new ReservationAccessDeniedException(reservationId);
    }

    reservation.cancel();
    return ReservationResult.from(reservation);
}
```

```java
// Request DTO — 클라이언트가 제어 가능한 값만, 제약과 함께
public record CreateReservationRequest(
        @NotNull Long shipId,
        @NotNull @Future LocalDateTime reservedAt,
        @Size(max = 200) String memo
) {
}
```

## 금지 예시

```java
// Mass Assignment — Entity 직접 바인딩 + 클라이언트가 role을 제어
@PostMapping
public Member create(@RequestBody Member member) {
    return memberRepository.save(member);
}
```

```java
// IDOR — 소유권 검증 없이 ID만으로 삭제
public void delete(Long reservationId) {
    reservationRepository.deleteById(reservationId);
}
```

```java
// 인젝션 — 사용자 입력 문자열 연결
@Query(value = "SELECT * FROM member WHERE name = '" + ":name" + "'", nativeQuery = true)
```

## 리뷰 심각도 기준

| 심각도 | 기준 | 예 |
|---|---|---|
| critical | 원격에서 곧바로 악용 가능한 결함 | 인젝션, 인증 없는 쓰기 엔드포인트, 시크릿 하드코딩 |
| major | 악용 조건이 있으나 실질적 위험 | IDOR(소유권 검증 누락), Mass Assignment 가능 구조, 민감정보 로깅·응답 노출 |
| minor | 심층 방어 결함, 즉시 악용은 어려움 | 검증 제약 누락, 예외 메시지 과다 정보, 404/403 구분 노출 |

critical 또는 major가 하나라도 있으면 NONPASS다.
