package com.example.be.project.dto.response;

import com.example.be.project.dto.result.ProjectResult;
import java.util.List;

/**
 * 프로젝트 단건 상세 API 응답.
 */
public record ProjectResponse(
        Long id,
        String name,
        String summary,
        List<String> members,
        String startDate,
        String endDate,
        String overview,
        String contribution,
        String conclusion,
        String troubleshooting,
        List<String> techStacks,
        String representativeImageUrl,
        List<String> imageUrls
) {
    public static ProjectResponse from(ProjectResult result) {
        return new ProjectResponse(
                result.id(),
                result.name(),
                result.summary(),
                result.members(),
                result.startDate().toString(),
                result.endDate().toString(),
                result.overview(),
                result.contribution(),
                result.conclusion(),
                result.troubleshooting(),
                result.techStacks(),
                result.representativeImageUrl(),
                result.imageUrls()
        );
    }
}
