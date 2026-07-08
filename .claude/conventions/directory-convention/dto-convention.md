# DTO 컨벤션

## 구조

```text
dto
├─ request
│  ├─ CreateReservationRequest.java
│  └─ ReservationSearchRequest.java
├─ result
│  └─ ReservationResult.java
└─ response
   └─ ReservationResponse.java
```

## MUST

- `dto` 하위에는 `request`, `result`, `response`만 생성한다.
- Java 파일명은 타입명과 동일한 PascalCase를 사용한다.
- DTO는 가능한 경우 Java `record`로 작성한다.
- DTO에 Entity 또는 영속성 프록시 타입을 필드로 포함하지 않는다.
- Request와 Response를 같은 타입으로 재사용하지 않는다.
- API 응답에 Result를 직접 노출하지 않는다.
- 별도의 `command`, `condition`, `common`, 중첩 `dto` 패키지를 만들지 않는다.
- 재사용이 가능한 dto는 최대한 재사용을 한다.

## Request

- Controller가 클라이언트로부터 받는 입력이다.
- Bean Validation 애너테이션은 Request에 작성한다.
- Controller는 Request를 Command Service에 그대로 전달할 수 있다.
- 검색·필터·페이징 조건 객체도 `request`에 둔다.

```java
public record CreateReservationRequest(
        @NotNull Long shipId,
        @NotNull @Future LocalDateTime reservedAt
) {
}
```

```java
public record ReservationSearchRequest(
        String keyword,
        LocalDate from,
        LocalDate to
) {
}
```

## Result

- Service가 Controller에 반환하는 계층 간 결과다.
- Command Service와 Query Service 모두 Result를 반환할 수 있다.
- Repository의 QueryDSL DTO Projection이 Result를 직접 반환할 수 있다.
- Result는 HTTP 상태, `ResponseEntity`, Servlet 타입에 의존하지 않는다.

```java
public record ReservationResult(
        Long id,
        String shipName,
        LocalDateTime reservedAt
) {
    public static ReservationResult from(Reservation reservation) {
        return new ReservationResult(
                reservation.getId(),
                reservation.getShip().getName(),
                reservation.getReservedAt()
        );
    }
}
```

`from(Entity)`는 Command Service가 이미 로딩한 Entity로 결과를 만드는 경우에 사용할 수 있다. 목록·복합 조회는 Entity 전체를 불러와 변환하지 말고 QueryDSL Projection을 우선한다.

## Response

- Controller가 클라이언트에 반환하는 API 표현이다.
- Result를 입력받는 `from` 정적 팩터리 메서드를 둔다.
- 날짜, 마스킹, 외부 노출 필드 등 API 표현 변환을 담당한다.
- 비즈니스 규칙이나 Repository 조회를 수행하지 않는다.

```java
public record ReservationResponse(
        Long id,
        String shipName,
        String reservedAt
) {
    public static ReservationResponse from(ReservationResult result) {
        return new ReservationResponse(
                result.id(),
                result.shipName(),
                result.reservedAt().toString()
        );
    }
}
```

## 변환 흐름

```text
Client
→ Request
→ Controller
→ Command/Query Service
→ Result
→ Controller
→ Response
→ Client
```

Domain은 이 흐름의 DTO를 참조하지 않는다.
