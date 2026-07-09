package com.example.be.techstack.controller;

import com.example.be.techstack.dto.request.CreateTechStackRequest;
import com.example.be.techstack.dto.request.UpdateTechStackRequest;
import com.example.be.techstack.dto.response.TechStackResponse;
import com.example.be.techstack.dto.result.TechStackResult;
import com.example.be.techstack.service.command.TechStackCommandService;
import com.example.be.techstack.service.query.TechStackQueryService;
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
@RequestMapping("/api/tech-stacks")
public class TechStackController {

    private final TechStackCommandService techStackCommandService;
    private final TechStackQueryService techStackQueryService;

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

    @GetMapping
    public ResponseEntity<List<TechStackResponse>> getTechStacks() {
        List<TechStackResponse> responses = techStackQueryService.getAll().stream()
                .map(TechStackResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{techStackId}")
    public ResponseEntity<TechStackResponse> get(
            @PathVariable Long techStackId
    ) {
        TechStackResult result = techStackQueryService.get(techStackId);
        return ResponseEntity.ok(TechStackResponse.from(result));
    }

    @PutMapping("/{techStackId}")
    public ResponseEntity<TechStackResponse> update(
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

    @DeleteMapping("/{techStackId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long techStackId
    ) {
        techStackCommandService.delete(techStackId);
        return ResponseEntity.noContent().build();
    }
}
