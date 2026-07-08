# Controller 컨벤션

## 책임

Controller는 HTTP 계층만 담당한다.

```text
HTTP Request
→ Request DTO 검증
→ Service 메서드 1회 호출
→ Result를 Response로 변환
→ HTTP Response
```

## MUST

- Controller는 Repository를 직접 주입하거나 호출하지 않는다.
- Controller에는 비즈니스 조건문, 상태 변경, 중복 검사, 권한 판단을 작성하지 않는다.
- 하나의 핸들러 메서드는 하나의 Service 메서드만 호출하는 것을 원칙으로 한다.
- 한 Controller 클래스가 Command Service와 Query Service를 모두 주입할 수는 있지만, 하나의 핸들러에서 둘을 조합하지 않는다.
- 요청 본문은 `@Valid`로 검증한다.
- 요청 DTO는 `dto.request`, 응답 DTO는 `dto.response`에 둔다.
- Service를 호출 할 때 `request` DTO를 모두 분리하여 Service의 파라미터로 준다.
- Service가 반환한 Result를 Response의 정적 팩터리 메서드로 변환한다.
- Entity 또는 Result를 API 응답으로 직접 반환하지 않는다.
- 예외를 핸들러마다 `try-catch`하지 않는다. 예상 가능한 비즈니스 예외는 전역 예외 처리기로 전달한다.

## 예시

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationResult result = reservationCommandService.create(request.name(), request.phone());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(result));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> get(
            @PathVariable Long reservationId
    ) {
        ReservationResult result = reservationQueryService.get(reservationId);
        return ResponseEntity.ok(ReservationResponse.from(result));
    }
}
```

## 금지 예시

```java
@PostMapping
public ReservationResponse create(CreateReservationRequest request) {
    if (reservationRepository.existsByName(request.name())) {
        throw new IllegalStateException();
    }

    Long id = reservationCommandService.create(request);
    ReservationResult result = reservationQueryService.get(id);
    return ReservationResponse.from(result);
}
```

위 코드는 Controller가 Repository와 비즈니스 흐름을 조합하므로 금지한다. 쓰기 결과에 필요한 값은 Command Service가 Result로 반환한다.

## HTTP 상태

- 생성 성공: `201 Created`
- 일반 조회·수정 성공: `200 OK`
- 응답 본문 없는 삭제 성공: `204 No Content`
- 오류 상태와 오류 응답은 `exception-convention.md`를 따른다.
