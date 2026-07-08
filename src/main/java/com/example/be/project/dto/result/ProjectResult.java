package com.example.be.project.dto.result;

import com.example.be.project.domain.Project;
import java.time.LocalDate;
import java.util.List;

/**
 * 프로젝트 단건 상세 결과. Command Service가 방금 저장한 Entity, Query Service가 조회한 Entity를
 * 서비스 경계 밖으로 전달하기 위한 변환 결과다.
 */
public record ProjectResult(
        Long id,
        String name,
        String summary,
        List<String> members,
        LocalDate startDate,
        LocalDate endDate,
        String overview,
        String contribution,
        String conclusion,
        String troubleshooting,
        List<String> techStacks,
        String representativeImageUrl,
        List<String> imageUrls
) {
    public static ProjectResult from(Project project) {
        return new ProjectResult(
                project.getId(),
                project.getName(),
                project.getSummary(),
                List.copyOf(project.getMembers()),
                project.getPeriod().getStartDate(),
                project.getPeriod().getEndDate(),
                project.getOverview(),
                project.getContribution(),
                project.getConclusion(),
                project.getTroubleshooting(),
                List.copyOf(project.getTechStacks()),
                project.getRepresentativeImageUrl(),
                project.getExtraImageUrls()
        );
    }
}
