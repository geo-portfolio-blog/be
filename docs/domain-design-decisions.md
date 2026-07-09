# 도메인 설계 결정 기록 (Project · TechStack · Experience)

> 이 문서는 도메인·API 재설계에서 내린 **확정 결정과 근거**를 남긴다. CLAUDE.md(아키텍처 컨벤션)와 별개로,
> "왜 이 필드/구조로 갔는지"를 다음에 봐도 알 수 있게 하기 위한 기록이다.
> **소스 오브 트루스는 FE(`../fe`) 화면이 요구하는 데이터**이며, 프로젝트 디자인은 리포 루트의
> `프로젝트 리스트 보기.png` / `프로젝트_상세보기_1~4.png`, 경력·기술 화면은 `커리어_리스트보기.png` /
> `experience_내_기술스택 리스트보기.png` 를 근거로 한다.

## 0. 관리 범위 (무엇을 DB로 두는가)

- **DB로 관리(계속 추가·변경됨)**: **Project · TechStack · Experience** → admin CRUD 대상.
- **FE 하드코딩(정적·1인 고정)**: Profile 페이지(이름/이메일/GPA/자격증/소개 카드/Development Mindset 등).
- 기준: **"앞으로 계속 늘어나느냐"**. 늘어나면 DB+CRUD, 고정이면 FE 하드코딩.

## 1. 공통 규약

- 리소스는 복수형 kebab-case: `/api/projects` · `/api/tech-stacks` · `/api/experiences`.
- **읽기(GET) 공개, 쓰기(POST/PUT/DELETE) ADMIN**.
- 세 도메인 모두 **CRUD 완성**: 생성(201) · 단건 조회(200) · 목록 · 수정(PUT=전체 교체, 200) · 삭제(204).
- 수정은 **PUT(전체 교체)** 의미. 부분 수정(PATCH)은 미도입.
- 레이어/DTO 3계층(Request→Result→Response)/QueryDSL 커스텀 리포지토리 패턴은 CLAUDE.md 그대로.

## 2. Experience (경력/학력 타임라인) — 신규

- `type`(enum `CAREER`/`EDUCATION`) · `title` · `organization`(nullable, 예: 고교 졸업은 소속 없음).
- 기간 `ExperiencePeriod`(@Embeddable): **`startDate` 필수 + `endDate` nullable**. 졸업 등 **단일 시점 허용**
  (Project의 `DevelopmentPeriod`는 종료일 필수라 재사용하지 않고 별도 값 객체로 분리).
- `points`(불릿): `@ElementCollection` + `@OrderColumn`(순서 보존). education 항목은 비어도 됨.
- `highlighted`(대표 강조 도트) · `sortOrder`(그룹 내 노출 순서).
- 목록: 순서 있는 단일 컬렉션(points)이라 fetch join 1개로 로딩 후 Java `distinct()`로 루트 중복 제거,
  유형→sortOrder→시작일 내림차순 정렬. 전체 `List` 반환(소규모 고정 목록).

## 3. TechStack (이력의 Technical Expertise) — 재설계

- 필드: `name` · `category`(enum) · **`note`(필수, 부가 설명)** · `imageUrl`(**nullable**) · `sortOrder`.
- **결정**: 기존 `proficiency`(1~5) **제거**(FE 미사용), `imageUrl`은 **nullable로 강등**(FE는 카테고리
  아이콘을 쓰고 스킬 이미지는 필수 요구 안 함), **`note` 추가**(FE 핵심).
- `category` enum = **`BACKEND` · `DEVOPS` · `OTHERS`** (FE의 Backend/DevOps/Others 3개에 정합.
  기존 `AI`/`ETC`는 정리). ⚠️ **Project의 카테고리 enum과는 별개**(값도 다름 — 아래 4 참조).
- `sortOrder` 추가 이유: 2차 정렬 기준이던 proficiency가 사라져 카테고리 내 정렬 기준이 필요.
- 목록: category→sortOrder→name 정렬, 전체 `List` 반환.

## 4. Project (목록 카드 + 케이스스터디 상세) — 재설계

### 확정 필드
- 식별/메타: `name` · **`slug`(라우팅용, unique)** · `summary` · **`category`(enum)** ·
  `thumbnailUrl` · **`team`(문자열)** · **`role`** · `githubUrl` · `period`(DevelopmentPeriod, 시작·종료 필수).
- 본문 섹션: `overview` · **`architecture`**(옛 `contribution`, "아키텍처 설계 이유(해결 방법)" 섹션) ·
  `conclusion`(서술) · **`metrics`**(지표) · **Troubleshooting**(구조화) · **`learnings`**(배운 점) ·
  **`techStacks`(표)** · `images`(대표 1 + 본문 N).

### 디자인/사용자 확정 사항 (근거: 상세보기 PNG 1~4)
- **category는 고정 enum** = **`BACKEND` · `DEVOPS` · `AI` · `ETC`(기타)** — 목록 상단 필터가
  `전체/Backend/DevOps/AI/기타`이기 때문. ("전체"는 필터 UI일 뿐 저장 값 아님.)
  → TechStack의 category enum과 **값·의미가 다르므로 별도 enum**으로 둔다.
- **`role`은 목록 카드에도 노출** → 목록 요약(Summary)에 포함.
- **팀 = `team` 자유 문자열** (예: "4인 (DevOps Lead)"). 팀원 이름 목록이 아니라 **인원수+메모 표시 문자열**.
  (Q1 = (b))
- **Tech Stack은 표(분류·기술·용도) 구조** = `ProjectTech`(@Embeddable) `{category, technology, purpose}`
  목록(@OrderColumn). **Expertise(TechStack 도메인)와 분리** — 내용이 비슷해도 목적이 달라 재사용하지 않고
  Project 애그리거트가 소유하는 값 객체로 둔다. **카드의 태그 칩은 이 표의 `technology` 값에서 뽑아 노출**.
- **Troubleshooting은 구조화** = `troubleshootingSituation`(상황·원인 본문) + `troubleshootingSolutions`
  (해결·결과 불릿, @OrderColumn). (Q2 = (a))
- **배운 점 = `learnings` `List<String>`**(여러 개 가능, @OrderColumn). FE는 "Learning" 블록으로 표시. (Q3)
- **이미지 = 대표 1장 + 본문 N장, 순서만 유지**. 어느 섹션에 넣을지는 FE가 배치(BE는 순서만).
  캡션은 디자인에 없어 미도입(필요 시 `ProjectImage`에 추가). (Q4 = (a))
- **결론 지표(metrics)는 선택**(0개 가능). **결론이 꼭 수치가 아닐 수 있음** — 지표가 없으면 `conclusion`
  텍스트/본문 이미지로 결론을 채운다. 지표 카드 화살표는 **`→`로 통일**(방향 필드 없음, before→after만 저장). (Q5)
- `metrics` = `Metric`(@Embeddable) `{label, before, after, description}` 목록(@OrderColumn).

### 목록 조회 = Slice
- **프로젝트 목록은 `Page`가 아니라 `Slice`** 로 제공(무한 스크롤/더 보기). 전체 count 쿼리 없이 `limit+1`로
  `hasNext`만 판단. 카드 태그(technology)는 페이지 id로 좁힌 별도 쿼리로 모아 조립(카테시안/ N+1 회피).
- 단건 상세는 **slug로 조회**(`GET /api/projects/{slug}`, FE `/projects/[slug]` 라우팅). 순서 있는 컬렉션이
  많아 애그리거트를 로딩해 `@OrderColumn` 순서를 살려 변환(읽기 트랜잭션 안에서 Result로 변환).
- 쓰기(PUT/DELETE)는 admin 작업이라 **id 기준**(`/api/projects/{projectId}`).

## 5. 열려 있는 항목 (TODO)

- **slug 중복**: 현재 DB unique 제약만 있어 중복 생성 시 깔끔한 400/409가 아니라 500으로 나갈 수 있음.
  우아한 처리(중복 검사 → 도메인 예외) 미도입.
- **런타임 미검증**: 컴파일 + QueryDSL Q타입 생성까지만 확인. MySQL 연결 후 실제 쿼리(Experience 목록의
  `@OrderColumn`+fetchJoin 정렬, Project 목록의 태그 조립/Slice)는 런타임 확인 필요. `BeApplicationTests`도
  DB 필요라 미실행.
- 이미지 캡션·섹션 바인딩은 필요 시 확장 여지로 남겨둠.
