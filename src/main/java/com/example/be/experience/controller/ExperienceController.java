package com.example.be.experience.controller;

import com.example.be.experience.dto.request.CreateExperienceRequest;
import com.example.be.experience.dto.request.UpdateExperienceRequest;
import com.example.be.experience.dto.response.ExperienceResponse;
import com.example.be.experience.dto.result.ExperienceResult;
import com.example.be.experience.service.command.ExperienceCommandService;
import com.example.be.experience.service.query.ExperienceQueryService;
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

@Tag(name = "Experience", description = "경력/학력 타임라인 조회 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/experiences")
public class ExperienceController {

    private final ExperienceCommandService experienceCommandService;
    private final ExperienceQueryService experienceQueryService;

    @Operation(
            summary = "경력/학력 생성",
            description = "새 경력 또는 학력 항목을 생성한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ExperienceResponse> create(
            @Valid @RequestBody CreateExperienceRequest request
    ) {
        ExperienceResult result = experienceCommandService.create(
                request.type(),
                request.title(),
                request.organization(),
                request.startDate(),
                request.endDate(),
                request.points(),
                request.highlighted(),
                request.sortOrder()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ExperienceResponse.from(result));
    }

    @Operation(
            summary = "경력/학력 목록 조회",
            description = "정렬된 전체 경력/학력 타임라인을 조회한다. 공개 API다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getExperiences() {
        List<ExperienceResponse> responses = experienceQueryService.getAll().stream()
                .map(ExperienceResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "경력/학력 단건 조회",
            description = "식별자로 경력/학력 한 건을 조회한다. 공개 API다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 항목 없음", content = @Content)
    })
    @GetMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> get(
            @Parameter(description = "경력/학력 식별자(PK)", example = "1")
            @PathVariable Long experienceId
    ) {
        ExperienceResult result = experienceQueryService.get(experienceId);
        return ResponseEntity.ok(ExperienceResponse.from(result));
    }

    @Operation(
            summary = "경력/학력 수정",
            description = "기존 경력/학력 항목을 수정한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 항목 없음", content = @Content)
    })
    @PutMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> update(
            @Parameter(description = "경력/학력 식별자(PK)", example = "1")
            @PathVariable Long experienceId,
            @Valid @RequestBody UpdateExperienceRequest request
    ) {
        ExperienceResult result = experienceCommandService.update(
                experienceId,
                request.type(),
                request.title(),
                request.organization(),
                request.startDate(),
                request.endDate(),
                request.points(),
                request.highlighted(),
                request.sortOrder()
        );
        return ResponseEntity.ok(ExperienceResponse.from(result));
    }

    @Operation(
            summary = "경력/학력 삭제",
            description = "경력/학력 항목을 삭제한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 항목 없음", content = @Content)
    })
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "경력/학력 식별자(PK)", example = "1")
            @PathVariable Long experienceId
    ) {
        experienceCommandService.delete(experienceId);
        return ResponseEntity.noContent().build();
    }
}
