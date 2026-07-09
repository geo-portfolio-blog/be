package com.example.be.experience.service.query;

import com.example.be.experience.dto.result.ExperienceResult;
import com.example.be.experience.exception.ExperienceNotFoundException;
import com.example.be.experience.repository.ExperienceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExperienceQueryService {

    private final ExperienceRepository experienceRepository;

    public List<ExperienceResult> getAll() {
        return experienceRepository.findAllResults();
    }

    public ExperienceResult get(Long experienceId) {
        return experienceRepository.findById(experienceId)
                .map(ExperienceResult::from)
                .orElseThrow(() -> new ExperienceNotFoundException(experienceId));
    }
}
