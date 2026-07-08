package com.example.be.techstack.controller;

import com.example.be.techstack.dto.request.CreateTechStackRequest;
import com.example.be.techstack.dto.response.TechStackResponse;
import com.example.be.techstack.dto.result.TechStackResult;
import com.example.be.techstack.service.command.TechStackCommandService;
import com.example.be.techstack.service.query.TechStackQueryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
                request.imageUrl(),
                request.proficiency()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TechStackResponse.from(result));
    }

    @GetMapping
    public ResponseEntity<List<TechStackResponse>> getTechStacks() {
        List<TechStackResult> results = techStackQueryService.getAll();
        List<TechStackResponse> responses = results.stream()
                .map(TechStackResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
