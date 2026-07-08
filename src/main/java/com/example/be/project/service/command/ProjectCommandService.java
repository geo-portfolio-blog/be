package com.example.be.project.service.command;

import com.example.be.project.domain.DevelopmentPeriod;
import com.example.be.project.domain.Project;
import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.repository.ProjectRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommandService {

    private final ProjectRepository projectRepository;

    public ProjectResult create(
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
            String thumbnailUrl,
            String representativeImageUrl,
            List<String> imageUrls
    ) {
        Project project = Project.create(
                name,
                summary,
                members,
                DevelopmentPeriod.of(startDate, endDate),
                overview,
                contribution,
                conclusion,
                troubleshooting,
                techStacks,
                thumbnailUrl,
                representativeImageUrl,
                imageUrls
        );

        Project saved = projectRepository.save(project);
        return ProjectResult.from(saved);
    }
}
