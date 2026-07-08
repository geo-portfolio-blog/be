# Service 컨벤션

## 구조

```text
service
├─ command
│  └─ ReservationCommandService.java
└─ query
   └─ ReservationQueryService.java
```

필요한 패키지만 생성한다. 읽기 기능만 있으면 `query`만, 쓰기 기능만 있으면 `command`만 생성할 수 있다.

## MUST

- 하나의 Service 클래스에 command와 query 유스케이스를 함께 작성하지 않는다.
- 네이밍은 `{Domain}CommandService`, `{Domain}QueryService`를 사용한다.
- Command Service는 생성·수정·삭제와 쓰기에 필요한 검증·조회·중복 확인을 담당한다.
- Query Service는 조회만 담당하고 Entity 상태를 변경하지 않는다.
- Controller가 한 요청을 처리하기 위해 Command Service와 Query Service를 조합하게 만들지 않는다.
- Service는 Entity를 Controller에 반환하지 않는다. 외부 반환 값은 Result DTO로 만든다.
- Domain 생성 메서드에 Request DTO 전체를 전달하지 않는다. Service가 Request에서 값을 꺼내 Domain 값으로 전달한다.
- Service 간 순환 의존을 만들지 않는다.
- 다른 도메인의 Service를 호출해 유스케이스를 조립하는 방식은 기본적으로 사용하지 않는다. 주 도메인의 Service가 필요한 Repository를 통해 검증 또는 Entity 조회를 수행한다.

## 트랜잭션

- Command Service는 클래스 또는 public 메서드에 `@Transactional`을 적용한다.
- Query Service는 클래스 또는 public 메서드에 `@Transactional(readOnly = true)`를 적용한다.
- `private` 메서드에 트랜잭션 경계를 기대하지 않는다.
- 외부 API 호출을 포함하는 긴 트랜잭션을 만들지 않는다. 외부 연동과 DB 변경의 순서를 명확히 설계한다.

## Command Service

Command는 쓰기에 필요한 조회를 할 수 있다. CQRS 분리는 “Command가 DB를 읽으면 안 된다”는 의미가 아니다.

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationCommandService {

    private final ReservationRepository reservationRepository;
    private final ShipRepository shipRepository;

    public ReservationResult create(Long shipId, LocalDateTime reservedAt) {
        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new ShipNotFoundException(shipId));

        Reservation reservation = Reservation.create(
                ship,
                reservedAt
        );

        Reservation saved = reservationRepository.save(reservation);
        return ReservationWResult.from(saved);
    }
}
```

## Query Service

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;

    public ReservationResult get(Long reservationId) {
        return reservationRepository.findResultById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }
}
```

## 반환 규칙

- 생성·수정·조회는 `dto.result`의 Result를 반환한다.
- 삭제 후 반환할 데이터가 없고 API가 `204 No Content`를 사용하면 `void`를 허용한다.
- 식별자 하나만 반환하더라도 Controller에 전달할 Service 결과라면 의미 있는 Result 타입을 우선한다.

```java
public record CreateReservationResult(Long reservationId) {
}
```

프로젝트가 하나의 `ReservationResult`로 생성·조회 응답을 모두 표현할 수 있으면 별도 생성 Result를 만들지 않아도 된다.

## 금지 예시

```java
public Reservation create(CreateReservationRequest request) {
    return reservationRepository.save(Reservation.from(request));
}
```

- Entity를 외부에 반환한다.
- Domain이 Request DTO에 의존할 가능성이 있다.
