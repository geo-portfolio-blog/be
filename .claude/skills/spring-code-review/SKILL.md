---
name: spring-code-review
description: "Spring Boot 기능/도메인 코드를 컨벤션대로 생성한 뒤 보안·성능 관점으로 검수·수정까지 한 번에 처리할 때 사용한다. '회원 도메인 CRUD 만들어줘', 'X 기능 API 추가하고 리뷰까지', '주문 서비스 코드 생성하고 보안/성능 점검' 같은 요청에서 실행된다(코드 생성+보안/성능 검수 파이프라인). 리뷰 없이 단순 코드 작성만 원하면 spring-convention-coder를 직접 쓰고 이 스킬은 쓰지 말 것. 컨벤션과 무관한 설정/문서/빌드 작업에도 쓰지 말 것."
argument-hint: "[생성할 기능/도메인 설명]"
allowed-tools: Read, Agent
---

# Spring Code Review Skill

기능/도메인 요청을 컨벤션대로 생성(spring-convention-coder)한 뒤,
보안(spring-security-reviewer)·성능(spring-performance-reviewer) 두 관점으로 병렬 검수하고,
nonpass면 코더를 재호출해 수정·재검수한다. 최종적으로 두 관점 판정과 발견을 보고한다.

- 워크스페이스: `_workspace/spring-code-review/`
- 리뷰 산출물: `_workspace/spring-code-review/security-review.md`, `_workspace/spring-code-review/performance-review.md`
- **최대 리뷰 라운드 2회**(코더는 최대 2회 호출: 최초 생성 1 + 수정 1). 오케스트레이션·루프·재시도는 이 스킬이 쥔다 — 에이전트끼리 서로 호출하지 않는다.

## Workflow

### 1. 입력 확인
사용자 요청에 생성할 기능/도메인 텍스트가 있는지 확인한다.
없으면 **"어떤 기능/도메인 코드를 생성할까요?"** 한 줄로 요청하고 종료한다.
`라운드 = 1`로 시작한다.

### 2. 코드 생성 (라운드 1) — `spring-convention-coder`
`Agent` 도구로 `spring-convention-coder`를 호출한다. 프롬프트에 문자 그대로 포함한다:
- 사용자 요청 원문(`$ARGUMENTS`).
- 워크스페이스 경로: `_workspace/spring-code-review/`.
- 지시: **"보고 말미에 작성/수정한 파일 경로 목록을 그대로 나열하라."**

코더 보고 말미의 **변경 파일 경로 목록**을 추출해 `변경파일목록`으로 보관한다.
목록이 비어 있으면(코드 생성 실패) 사유를 사용자에게 전달하고 종료한다.

### 3. 병렬 리뷰
`spring-security-reviewer`와 `spring-performance-reviewer`를 **한 번의 Agent 배치로 동시 호출**한다(상호 의존 없음, 서로 다른 출력 파일).

`spring-security-reviewer` 프롬프트에 문자 그대로 전달:
- 리뷰 범위(변경 파일 경로 목록): `변경파일목록`.
- 출력 파일 경로: `_workspace/spring-code-review/security-review.md`.

`spring-performance-reviewer` 프롬프트에 문자 그대로 전달:
- 리뷰 범위(변경 파일 경로 목록): `변경파일목록`.
- 출력 파일 경로: `_workspace/spring-code-review/performance-review.md`.

### 4. 판정 취합
`Read`로 두 리뷰 파일의 `## 종합 판정` 첫 토큰을 읽는다:
- `_workspace/spring-code-review/security-review.md`
- `_workspace/spring-code-review/performance-review.md`

토큰이 `PASS` 또는 `NONPASS`가 아니거나 파일이 없으면(형식 붕괴) 해당 관점을 **NONPASS로 취급**한다.
판정은 리뷰어 몫이다 — 스킬은 토큰만 파싱해 분기한다.

- **둘 다 PASS** → 7단계(최종 보고, 성공).
- **하나라도 NONPASS**:
  - `라운드 == 1`이면 5단계로 간다.
  - `라운드 == 2`이면 실패 종료한다. 두 리뷰 파일의 미해결 발견(심각도·파일·설명)을 심각도순으로 그대로 나열하고 **"수동 개입 권고"**로 정직하게 종료한다.

### 5. 수정 (라운드 2) — `spring-convention-coder`
`라운드 = 2`로 올린다. `Agent` 도구로 `spring-convention-coder`를 재호출한다. 프롬프트에 문자 그대로 포함한다:
- NONPASS인 리뷰 파일 경로(둘 중 NONPASS인 것만 전달):
  - 보안 NONPASS → `_workspace/spring-code-review/security-review.md`
  - 성능 NONPASS → `_workspace/spring-code-review/performance-review.md`
- 지시: **"이 리뷰 파일의 발견을 반영해 코드를 수정하고, 보고 말미에 수정한 파일 경로 목록을 그대로 나열하라."**

코더 보고 말미의 수정 파일 목록으로 `변경파일목록`을 갱신한다.

### 6. 재리뷰
3단계를 다시 수행한다(두 리뷰어가 같은 출력 파일에 덮어씀). 그 후 4단계로 돌아가 판정한다.
`라운드 == 2`이므로 여전히 NONPASS가 남으면 4단계 규칙에 따라 실패 종료한다.

### 7. 최종 보고
`Read`로 두 리뷰 파일을 읽어 사용자에게 보고한다:
- **생성/수정 파일**: `변경파일목록`.
- **최종 판정**: 보안 PASS/NONPASS, 성능 PASS/NONPASS.
- **발견 요약**: 두 리뷰의 발견을 `critical > major > minor` 심각도순으로 정리.
- 성공(둘 다 PASS)이면 그 사실을, 실패 종료면 미해결 발견과 **"수동 개입 권고"**를 명시한다.

## 원칙
- 이름·경로는 실제 파일이 정본이다. 에이전트 이름: `spring-convention-coder`, `spring-security-reviewer`, `spring-performance-reviewer`.
- 리뷰어 출력 경로 == 라운드2 코더 재호출 입력 경로(문자 그대로 동일).
- 리뷰는 자문 판정이다. 스킬은 판정 토큰을 파싱해 분기만 하며 코드를 직접 수정하지 않는다.
- 판정 토큰을 못 낸 형식 붕괴는 NONPASS로 취급한다.
- 리뷰 라운드 최대 2회 + 라운드2 NONPASS 시 실패 종료 — 무한 루프 없음.
- `disable-model-invocation`은 기본값(false) 유지 — 코드 생성은 git으로 되돌릴 수 있어 명시 호출 전용으로 잠글 필요가 없다(team-spec 결정).
