# Domain 컨벤션

## 범위

`{domain}.domain`에는 다음 타입을 직접 배치한다.

- JPA Entity
- Value Object
- 도메인 Enum
- 도메인 상태 변경과 불변식 검증에 필요한 타입

별도 하위 패키지는 만들지 않는다.

## MUST

- Domain은 `controller`, `service`, `dto`를 import하지 않는다.
- Entity 생성 메서드에 Request DTO 전체를 전달하지 않는다.
- Setter를 공개하지 않는다.
- Lombok `@Data`를 Entity에 사용하지 않는다.
- 기본 생성자는 JPA를 위해 `protected`로 둔다.
- 생성은 생성자 또는 의미 있는 정적 팩터리 메서드로 제한한다.
- 상태 변경은 의미 있는 도메인 메서드로 수행한다.
- Entity 내부에 HTTP, Servlet, `ResponseEntity` 등 웹 타입을 사용하지 않는다.
- Entity가 Repository 또는 Service를 직접 호출하지 않는다.

## 예시

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Ship ship;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    private Reservation(Ship ship, LocalDateTime reservedAt) {
        validateReservedAt(reservedAt);
        this.ship = ship;
        this.reservedAt = reservedAt;
    }

    public static Reservation create(Ship ship, LocalDateTime reservedAt) {
        return new Reservation(ship, reservedAt);
    }

    public void changeReservedAt(LocalDateTime reservedAt) {
        validateReservedAt(reservedAt);
        this.reservedAt = reservedAt;
    }

    private void validateReservedAt(LocalDateTime reservedAt) {
        if (reservedAt == null) {
            throw new IllegalArgumentException("예약 일시는 필수입니다.");
        }
    }
}
```

## 연관관계

- 기본 fetch 전략은 `LAZY`를 사용한다.
- 양방향 연관관계는 실제 탐색 요구가 명확할 때만 사용한다.
- 연관관계 편의 메서드는 양쪽 상태를 일관되게 변경한다.
- API 직렬화를 위해 Entity 연관관계에 Jackson 애너테이션을 덧붙이지 않는다. Response DTO를 사용한다.

## 검증 책임

- 형식·필수값 검증은 Request DTO에서 수행한다.
- 도메인 불변식과 상태 전이 검증은 Domain에서 수행한다.
- 중복 여부, 존재 여부, 권한처럼 Repository 조회가 필요한 검증은 Service에서 수행한다.

## 예외

- 단순 인자 자체가 잘못된 내부 호출은 `IllegalArgumentException`을 사용할 수 있다.
- API 오류 코드가 필요한 비즈니스 실패는 Service 또는 Domain에서 `{domain}.exception`의 구체적인 예외를 발생시킨다.
- Domain이 Spring의 `HttpStatus`에 직접 의존하지 않도록 한다.
