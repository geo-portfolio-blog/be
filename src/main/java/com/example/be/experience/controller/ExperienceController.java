package com.example.be.experience.controller;

import com.example.be.experience.dto.request.CreateExperienceRequest;
import com.example.be.experience.dto.request.UpdateExperienceRequest;
import com.example.be.experience.dto.response.ExperienceResponse;
import com.example.be.experience.dto.result.ExperienceResult;
import com.example.be.experience.service.command.ExperienceCommandService;
import com.example.be.experience.service.query.ExperienceQueryService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/experiences")
public class ExperienceController {

    private final ExperienceCommandService experienceCommandService;
    private final ExperienceQueryService experienceQueryService;

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

    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getExperiences() {
        List<ExperienceResponse> responses = experienceQueryService.getAll().stream()
                .map(ExperienceResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> get(
            @PathVariable Long experienceId
    ) {
        ExperienceResult result = experienceQueryService.get(experienceId);
        return ResponseEntity.ok(ExperienceResponse.from(result));
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> update(
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

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long experienceId
    ) {
        experienceCommandService.delete(experienceId);
        return ResponseEntity.noContent().build();
    }
}
