package com.example.be.project.controller;

import com.example.be.project.dto.request.CreateProjectRequest;
import com.example.be.project.dto.request.MetricRequest;
import com.example.be.project.dto.request.ProjectTechRequest;
import com.example.be.project.dto.request.TroubleshootingRequest;
import com.example.be.project.dto.request.UpdateProjectRequest;
import com.example.be.project.dto.response.ProjectResponse;
import com.example.be.project.dto.response.ProjectSummaryResponse;
import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.dto.result.ProjectSummaryResult;
import com.example.be.project.service.command.MetricCommand;
import com.example.be.project.service.command.ProjectCommandService;
import com.example.be.project.service.command.ProjectTechCommand;
import com.example.be.project.service.query.ProjectQueryService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
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

@Tag(name = "Project", description = "프로젝트 목록/케이스스터디 조회 및 관리(생성·수정·삭제) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectCommandService projectCommandService;
    private final ProjectQueryService projectQueryService;

    @Operation(
            summary = "프로젝트 생성",
            description = "새 프로젝트(케이스스터디)를 생성한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request
    ) {
        TroubleshootingRequest troubleshooting = request.troubleshooting();
        ProjectResult result = projectCommandService.create(
                request.name(),
                request.slug(),
                request.summary(),
                request.category(),
                request.team(),
                request.role(),
                request.startDate(),
                request.endDate(),
                request.githubUrl(),
                request.overview(),
                request.architecture(),
                request.conclusion(),
                toMetricCommands(request.metrics()),
                troubleshooting == null ? null : troubleshooting.situationCause(),
                troubleshooting == null ? null : troubleshooting.solutions(),
                request.learnings(),
                toTechCommands(request.techStacks()),
                request.thumbnailUrl(),
                request.representativeImageUrl(),
                request.imageUrls()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProjectResponse.from(result));
    }

    @Operation(
            summary = "프로젝트 상세 조회",
            description = "slug로 프로젝트 케이스스터디 상세를 조회한다. 공개 API다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 slug의 프로젝트 없음", content = @Content)
    })
    @GetMapping("/{slug}")
    public ResponseEntity<ProjectResponse> get(
            @Parameter(description = "프로젝트 slug(라우팅 식별자)", example = "geo-portfolio")
            @PathVariable String slug
    ) {
        ProjectResult result = projectQueryService.getBySlug(slug);
        return ResponseEntity.ok(ProjectResponse.from(result));
    }

    @Operation(
            summary = "프로젝트 목록 조회",
            description = "프로젝트 요약 카드 목록을 페이징으로 조회한다. 공개 API다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<Slice<ProjectSummaryResponse>> getProjects(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Slice<ProjectSummaryResult> results = projectQueryService.getSummaries(pageable);
        return ResponseEntity.ok(results.map(ProjectSummaryResponse::from));
    }

    @Operation(
            summary = "프로젝트 수정",
            description = "기존 프로젝트를 수정한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력 검증 실패", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 프로젝트 없음", content = @Content)
    })
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> update(
            @Parameter(description = "프로젝트 식별자(PK)", example = "1")
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        TroubleshootingRequest troubleshooting = request.troubleshooting();
        ProjectResult result = projectCommandService.update(
                projectId,
                request.name(),
                request.slug(),
                request.summary(),
                request.category(),
                request.team(),
                request.role(),
                request.startDate(),
                request.endDate(),
                request.githubUrl(),
                request.overview(),
                request.architecture(),
                request.conclusion(),
                toMetricCommands(request.metrics()),
                troubleshooting == null ? null : troubleshooting.situationCause(),
                troubleshooting == null ? null : troubleshooting.solutions(),
                request.learnings(),
                toTechCommands(request.techStacks()),
                request.thumbnailUrl(),
                request.representativeImageUrl(),
                request.imageUrls()
        );
        return ResponseEntity.ok(ProjectResponse.from(result));
    }

    @Operation(
            summary = "프로젝트 삭제",
            description = "프로젝트를 삭제한다. 관리자 인증이 필요하다.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패(관리자 자격 증명 필요)", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 프로젝트 없음", content = @Content)
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "프로젝트 식별자(PK)", example = "1")
            @PathVariable Long projectId
    ) {
        projectCommandService.delete(projectId);
        return ResponseEntity.noContent().build();
    }

    private List<MetricCommand> toMetricCommands(List<MetricRequest> metrics) {
        if (metrics == null) {
            return List.of();
        }
        return metrics.stream()
                .map(metric -> new MetricCommand(metric.label(), metric.before(), metric.after(), metric.description()))
                .toList();
    }

    private List<ProjectTechCommand> toTechCommands(List<ProjectTechRequest> techStacks) {
        if (techStacks == null) {
            return List.of();
        }
        return techStacks.stream()
                .map(tech -> new ProjectTechCommand(tech.category(), tech.technology(), tech.purpose()))
                .toList();
    }
}
