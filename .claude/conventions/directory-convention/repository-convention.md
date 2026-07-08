# Repository 컨벤션

## 구조

```text
repository
├─ ReservationRepository.java
├─ ReservationRepositoryCustom.java
└─ ReservationRepositoryCustomImpl.java
```

필요한 경우에만 Custom 인터페이스와 구현체를 생성한다. 단순 CRUD만 필요하면 `ReservationRepository` 하나만 둔다.

## MUST

- 기본 Repository 이름은 `{Domain}Repository`로 한다.
- `{Domain}Repository`는 `JpaRepository`를 상속한다.
- QueryDSL fragment가 있으면 `{Domain}Repository`가 `{Domain}RepositoryCustom`도 함께 상속한다.
- fragment 구현체 이름은 `{Domain}RepositoryCustomImpl`로 한다.
- 별도 `QueryRepository`, `Dao`, `Mapper` 클래스를 만들지 않는다.
- Repository는 Service 또는 Controller를 참조하지 않는다.
- 단건 조회 실패를 표현하기 위해 Service 밖으로 `null`을 노출하지 않는다. `Optional`을 사용한다.
- 조회 전용 Projection은 `dto.result`를 반환할 수 있다.

## 기본 Repository

```java
public interface ReservationRepository extends
        JpaRepository<Reservation, Long>,
        ReservationRepositoryCustom {

    boolean existsByShipIdAndReservedAt(Long shipId, LocalDateTime reservedAt);
}
```

- 저장, 삭제, Entity 기반 조회와 간단한 derived query를 담당한다.
- Command Service가 상태 변경을 위해 Entity를 조회할 때 사용한다.
- 필요한 Entity 그래프는 명시적인 JPQL `@Query` 또는 `@EntityGraph`로 표현할 수 있다.

## Custom fragment

```java
public interface ReservationRepositoryCustom {

    Optional<ReservationResult> findResultById(Long reservationId);

    List<ReservationResult> findResults(ReservationSearchRequest request);
}
```

검색 조건 타입은 `dto.request`, Projection 반환 타입은 `dto.result`에 둔다.

## QueryDSL 구현체

```java
@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl
        implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ReservationResult> findResultById(Long reservationId) {
        ReservationResult result = queryFactory
                .select(Projections.constructor(
                        ReservationResult.class,
                        reservation.id,
                        reservation.ship.name,
                        reservation.reservedAt
                ))
                .from(reservation)
                .join(reservation.ship, ship)
                .where(reservation.id.eq(reservationId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
```

Spring Data가 Repository fragment를 조합하므로 `ReservationRepository`가 Custom 인터페이스를 상속해야 한다.

## Projection 규칙

- 목록·복합 조회에서 Entity 전체를 조회한 뒤 Mapper로 Result를 만드는 방식을 사용하지 않는다.
- QueryDSL 조회 시점에 Result DTO로 Projection한다.
- 기본 방식은 `Projections.constructor()`로 통일한다.
- 생성자 인자 순서와 Result record 컴포넌트 순서를 반드시 일치시킨다.
- Request → Domain, Result → Response 변환은 이 Projection 규칙의 대상이 아니다.

## Repository 사용

Service에서는 기본 Repository 타입 하나를 주입한다.

```java
private final ReservationRepository reservationRepository;
```

다음과 같이 `ReservationRepositoryCustom` 구현체를 별도 Bean처럼 직접 주입하지 않는다.

```java
private final ReservationRepositoryCustom reservationRepositoryCustom;
```

## fetch join 기준

- Command에서 변경할 Entity와 필요한 연관 Entity를 함께 로딩할 때 JPQL fetch join 또는 `@EntityGraph`를 사용할 수 있다.
- 화면·API 조회를 위한 복합 데이터는 QueryDSL Projection을 우선한다.
- 컬렉션 fetch join과 페이징을 무분별하게 함께 사용하지 않는다.
