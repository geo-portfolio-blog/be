package com.example.be.techpart.controller;

import com.example.be.techpart.dto.request.CreateTechPartRequest;
import com.example.be.techpart.dto.request.TechSkillRequest;
import com.example.be.techpart.dto.request.UpdateTechPartRequest;
import com.example.be.techpart.dto.response.TechPartResponse;
import com.example.be.techpart.dto.result.TechPartResult;
import com.example.be.techpart.service.command.TechPartCommandService;
import com.example.be.techpart.service.command.TechSkillCommand;
import com.example.be.techpart.service.query.TechPartQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TechPart", description = "Technical Expertise(기술 역량) 분류/스킬 조회 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tech-parts")
public class TechPartController {

    private final TechPartCommandService techPartCommandService;
    private final TechPartQueryService techPartQueryService;

    @Operation(
            summary = "기술 분류 생성",
            description = "분류(Part)와 그에 속한 스킬 목록을 함께 생성한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TechPartResponse> create(
            @Valid @RequestBody CreateTechPartRequest request
    ) {
        TechPartResult result = techPartCommandService.create(
                request.name(),
                request.sortOrder(),
                toSkillCommands(request.skills())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TechPartResponse.from(result));
    }

    @Operation(
            summary = "기술 분류 목록 조회",
            description = "정렬된 전체 분류를 스킬 목록과 함께 조회한다. 공개 API다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<TechPartResponse>> getTechParts() {
        List<TechPartResponse> responses = techPartQueryService.getAll().stream()
                .map(TechPartResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "기술 분류 단건 조회",
            description = "식별자로 분류 한 건을 스킬 목록과 함께 조회한다. 공개 API다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 분류 없음", content = @Content)
    })
    @GetMapping("/{techPartId}")
    public ResponseEntity<TechPartResponse> get(
            @Parameter(description = "기술 분류 식별자(PK)", example = "1")
            @PathVariable Long techPartId
    ) {
        TechPartResult result = techPartQueryService.get(techPartId);
        return ResponseEntity.ok(TechPartResponse.from(result));
    }

    @Operation(
            summary = "기술 분류 수정",
            description = "분류와 스킬 목록을 통째로 새 값으로 교체한다(PUT). 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 분류 없음", content = @Content)
    })
    @PutMapping("/{techPartId}")
    public ResponseEntity<TechPartResponse> update(
            @Parameter(description = "기술 분류 식별자(PK)", example = "1")
            @PathVariable Long techPartId,
            @Valid @RequestBody UpdateTechPartRequest request
    ) {
        TechPartResult result = techPartCommandService.update(
                techPartId,
                request.name(),
                request.sortOrder(),
                toSkillCommands(request.skills())
        );
        return ResponseEntity.ok(TechPartResponse.from(result));
    }

    @Operation(
            summary = "기술 분류 삭제",
            description = "분류를 스킬 목록과 함께 삭제한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 분류 없음", content = @Content)
    })
    @DeleteMapping("/{techPartId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "기술 분류 식별자(PK)", example = "1")
            @PathVariable Long techPartId
    ) {
        techPartCommandService.delete(techPartId);
        return ResponseEntity.noContent().build();
    }

    private List<TechSkillCommand> toSkillCommands(List<TechSkillRequest> skills) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream()
                .map(skill -> new TechSkillCommand(skill.name(), skill.note()))
                .toList();
    }
}
