package com.example.be.techstack.controller;

import com.example.be.techstack.dto.request.CreateTechStackRequest;
import com.example.be.techstack.dto.request.UpdateTechStackRequest;
import com.example.be.techstack.dto.response.TechStackResponse;
import com.example.be.techstack.dto.result.TechStackResult;
import com.example.be.techstack.service.command.TechStackCommandService;
import com.example.be.techstack.service.query.TechStackQueryService;
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

@Tag(name = "TechStack", description = "기술 역량(Technical Expertise) 조회 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tech-stacks")
public class TechStackController {

    private final TechStackCommandService techStackCommandService;
    private final TechStackQueryService techStackQueryService;

    @Operation(
            summary = "기술 스택 생성",
            description = "새 기술 스택 항목을 생성한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TechStackResponse> create(
            @Valid @RequestBody CreateTechStackRequest request
    ) {
        TechStackResult result = techStackCommandService.create(
                request.name(),
                request.category(),
                request.note(),
                request.imageUrl(),
                request.sortOrder()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TechStackResponse.from(result));
    }

    @Operation(
            summary = "기술 스택 목록 조회",
            description = "정렬된 전체 기술 스택 목록을 조회한다. 공개 API다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<TechStackResponse>> getTechStacks() {
        List<TechStackResponse> responses = techStackQueryService.getAll().stream()
                .map(TechStackResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "기술 스택 단건 조회",
            description = "식별자로 기술 스택 한 건을 조회한다. 공개 API다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 기술 스택 없음", content = @Content)
    })
    @GetMapping("/{techStackId}")
    public ResponseEntity<TechStackResponse> get(
            @Parameter(description = "기술 스택 식별자(PK)", example = "1")
            @PathVariable Long techStackId
    ) {
        TechStackResult result = techStackQueryService.get(techStackId);
        return ResponseEntity.ok(TechStackResponse.from(result));
    }

    @Operation(
            summary = "기술 스택 수정",
            description = "기존 기술 스택을 수정한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 기술 스택 없음", content = @Content)
    })
    @PutMapping("/{techStackId}")
    public ResponseEntity<TechStackResponse> update(
            @Parameter(description = "기술 스택 식별자(PK)", example = "1")
            @PathVariable Long techStackId,
            @Valid @RequestBody UpdateTechStackRequest request
    ) {
        TechStackResult result = techStackCommandService.update(
                techStackId,
                request.name(),
                request.category(),
                request.note(),
                request.imageUrl(),
                request.sortOrder()
        );
        return ResponseEntity.ok(TechStackResponse.from(result));
    }

    @Operation(
            summary = "기술 스택 삭제",
            description = "기술 스택을 삭제한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 기술 스택 없음", content = @Content)
    })
    @DeleteMapping("/{techStackId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "기술 스택 식별자(PK)", example = "1")
            @PathVariable Long techStackId
    ) {
        techStackCommandService.delete(techStackId);
        return ResponseEntity.noContent().build();
    }
}
