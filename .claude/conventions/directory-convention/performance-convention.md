# Performance 컨벤션

Spring Data JPA·QueryDSL 코드가 지켜야 할 성능 규칙이다. 이 문서는 `spring-performance-reviewer`의
판정 정본이며, `repository-convention.md`·`service-convention.md`의 구조 규칙을 성능 관점에서 보강한다.

## MUST

- 연관관계 fetch 전략은 `LAZY`를 기본으로 한다. `@ManyToOne`·`@OneToOne`에 `EAGER`를 두지 않는다
  (`domain-convention.md`와 동일).
- 목록·복합 조회는 Entity 전체를 로딩한 뒤 변환하지 않는다. QueryDSL `Projections.constructor()`로
  `dto.result`에 직접 Projection한다 (`repository-convention.md`와 동일).
- 반복문 안에서 지연 로딩 연관을 순회 접근하는 코드(N+1)를 만들지 않는다. 연관 데이터가 필요한 조회는
  Projection의 join 또는 fetch join·`@EntityGraph`로 한 번에 가져온다.
- 컬렉션(`@OneToMany`) fetch join과 페이징을 함께 사용하지 않는다. (Hibernate가 전체 로딩 후 메모리에서
  페이징한다 — `HHH000104`) 페이징이 필요한 컬렉션 조회는 ID 페이징 후 IN 조회 또는 `@BatchSize`를 사용한다.
- 둘 이상의 컬렉션을 한 쿼리에서 fetch join하지 않는다. (카테시안 곱 폭발)
- 사용자 입력·데이터 증가에 따라 결과가 무한히 커질 수 있는 목록 API는 페이징(`Pageable` 또는
  offset/limit)을 적용한다. 전체 목록 반환은 결과 크기가 구조적으로 유한할 때만 허용한다.
- Query Service는 `@Transactional(readOnly = true)`를 적용한다. (더티 체킹 스냅샷 생략, flush 억제 —
  `service-convention.md`와 동일)
- 트랜잭션 범위를 필요 이상으로 넓히지 않는다. 외부 API 호출·파일 I/O를 트랜잭션 안에 두지 않는다.
- 존재 여부만 필요하면 Entity를 조회하지 않고 `existsBy...` 또는 `count`를 사용한다.
- 건수만 필요하면 목록을 조회해 `size()`를 세지 않고 count 쿼리를 사용한다.
- 단건 수정은 조회 후 도메인 메서드로 변경한다(더티 체킹). 단, 대량 일괄 변경은 건별 루프 대신
  벌크 연산(`update ... where`)을 사용하고, 벌크 후 영속성 컨텍스트를 정리한다.
- `saveAll` 대상이 수백 건 이상으로 커질 수 있으면 배치 삽입 설정(`hibernate.jdbc.batch_size`,
  IDENTITY 전략 제약)을 검토하고, 검토 결과를 보고에 남긴다.
- WHERE·JOIN·ORDER BY에 반복 사용되는 컬럼은 인덱스를 고려한다. Entity에 `@Table(indexes = ...)`로
  명시하거나, 인덱스가 필요한 이유를 보고에 남긴다.
- 조회 결과를 애플리케이션 메모리에서 필터링·정렬하지 않는다. 조건과 정렬은 쿼리로 내린다.

## 예시

```java
// 목록 조회 — Projection + 페이징
@Override
public List<ReservationResult> findResults(ReservationSearchRequest request, Pageable pageable) {
    return queryFactory
            .select(Projections.constructor(
                    ReservationResult.class,
                    reservation.id,
                    reservation.ship.name,
                    reservation.reservedAt
            ))
            .from(reservation)
            .join(reservation.ship, ship)
            .where(keywordContains(request.keyword()))
            .orderBy(reservation.reservedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
}
```

```java
// 존재 확인 — Entity 로딩 없이
if (reservationRepository.existsByShipIdAndReservedAt(shipId, reservedAt)) {
    throw new DuplicateReservationException(shipId, reservedAt);
}
```

## 금지 예시

```java
// N+1 — 목록 로딩 후 루프에서 지연 연관 접근
List<Reservation> reservations = reservationRepository.findAll();
return reservations.stream()
        .map(r -> new ReservationResult(r.getId(), r.getShip().getName(), r.getReservedAt()))
        .toList();
```

```java
// 컬렉션 fetch join + 페이징 — 메모리 페이징 발생
@Query("select s from Ship s join fetch s.reservations")
Page<Ship> findAllWithReservations(Pageable pageable);
```

```java
// 메모리 필터링 — 조건을 쿼리로 내리지 않음
reservationRepository.findAll().stream()
        .filter(r -> r.getReservedAt().isAfter(from))
        .toList();
```

## 리뷰 심각도 기준

| 심각도 | 기준 | 예 |
|---|---|---|
| critical | 데이터 증가 시 장애 수준으로 악화 | 컬렉션 fetch join+페이징, 무한 목록 API, 전체 로딩 후 메모리 필터링 |
| major | 뚜렷한 확장성 결함 | N+1 접근 패턴, EAGER 선언, 목록 조회 Projection 미적용, readOnly 누락, 외부 호출 포함 트랜잭션 |
| minor | 개선 여지, 당장 영향 제한적 | exists/count 미활용, 인덱스 검토 누락, 배치 설정 미검토 |

critical 또는 major가 하나라도 있으면 NONPASS다.
