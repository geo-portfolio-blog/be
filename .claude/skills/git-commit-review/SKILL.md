---
name: git-commit-review
description: "현재 작업 트리의 변경사항을 커밋하고 그 커밋을 코드 리뷰해 보고할 때 사용한다. '변경사항 커밋하고 리뷰해줘', '작업한 거 커밋 후 코드 리뷰' 같이 요청하며 `/git-commit-review`로 명시 호출할 때 실행된다. git-committer가 스테이징·커밋 메시지 작성·커밋을 수행하고, commit-reviewer가 diff를 검토하며, 스킬이 결과를 심각도순으로 요약 보고한다. 커밋은 부작용이 있으므로 자동 호출을 막고 사용자 명시 호출만 허용한다. 커밋 없이 코드 리뷰만 원하면 이 스킬을 쓰지 말 것."
argument-hint: "[커밋 범위·메시지 힌트 (선택)]"
disable-model-invocation: true
allowed-tools: Read, Bash, Agent
---

# Git Commit Review Skill

작업 트리 변경사항을 커밋한 뒤 그 커밋을 코드 리뷰해 보고한다.
커밋(git-committer) → 리뷰(commit-reviewer) → 사용자 보고 순으로 진행한다.
중간 산출물은 `_workspace/git-commit-review/`에 둔다.
reviewer는 게이팅 판정자가 아니라 자문 리포트만 내므로 **재시도 루프는 없다.**

## Workflow

1. **입력 확인.**
   `git status --porcelain`을 `Bash`로 실행해 커밋할 변경이 있는지 확인한다.
   출력이 비어 있으면 사용자에게 **"커밋할 변경사항이 없습니다"**를 알리고 종료한다.

2. **커밋 — `git-committer` 호출.**
   `Agent` 도구로 `git-committer`를 호출한다. 프롬프트에 다음을 문자 그대로 포함한다:
   - 사용자 지시(`$ARGUMENTS`의 커밋 범위 한정·메시지 힌트). 지시가 없으면
     **"전체 변경을 AngularJS Commit Convention에 맞춰 커밋"**하라고 전달한다.
   - 워크스페이스 경로: `_workspace/git-commit-review/`.
   - 요구 산출물: `_workspace/git-commit-review/commit-info.md`(커밋 해시·최종 메시지·파일별
     +/- 통계), `_workspace/git-commit-review/commit.diff`(방금 만든 커밋의 순수 diff).

   committer가 **실패**(커밋할 변경 없음, git 오류, 충돌, 커밋 미생성)를 보고하면
   그 사유를 사용자에게 **그대로 전달하고 종료**한다. 파이프라인을 이어가지 않는다.

3. **리뷰 — `commit-reviewer` 호출.**
   `Agent` 도구로 `commit-reviewer`를 호출한다. 프롬프트에 다음 경로를 문자 그대로 전달한다:
   - 검토 대상 diff: `_workspace/git-commit-review/commit.diff`
   - 커밋 메타데이터: `_workspace/git-commit-review/commit-info.md`
   - 요구 산출물: `_workspace/git-commit-review/review.md`(종합 판정 + 심각도순 발견 목록).

   commit.diff가 없으면 3단계로 오지 않는다(2단계 실패 종료). 리뷰는 커밋을 되돌리지 않는다.

4. **보고.**
   `Read`로 `_workspace/git-commit-review/review.md`를 읽는다. 사용자에게 제시한다:
   - **커밋 요약**: 해시, 커밋 제목, 변경 파일 수(review.md / commit-info.md 기준).
   - **리뷰 발견**: `critical > major > minor > nit` 순으로 정리.
   - `critical` 발견이 하나라도 있으면 **후속 수정 커밋을 권고**한다.
   - 발견이 없으면 "리뷰 관점에서 문제를 찾지 못함"을 명시한다.

## 원칙

- 오케스트레이션(순서·실패 종료)은 이 스킬이 쥔다. 실제 git 작업은 git-committer가 수행한다.
- 이름·경로는 실제 파일이 정본이다. 에이전트 이름은 `git-committer`, `commit-reviewer`.
- 커밋 실패는 정직하게 사유를 전달하고 즉시 종료한다. 재커밋을 자동 시도하지 않는다.
- 리뷰는 자문이다. 판정을 근거로 커밋을 되돌리거나 게이팅하지 않는다.
