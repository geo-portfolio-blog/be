package com.example.be.experience.service.command;

import com.example.be.experience.domain.Experience;
import com.example.be.experience.domain.ExperiencePeriod;
import com.example.be.experience.domain.ExperienceType;
import com.example.be.experience.dto.result.ExperienceResult;
import com.example.be.experience.exception.ExperienceNotFoundException;
import com.example.be.experience.repository.ExperienceRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExperienceCommandService {

    private final ExperienceRepository experienceRepository;

    public ExperienceResult create(
            ExperienceType type,
            String title,
            String organization,
            LocalDate startDate,
            LocalDate endDate,
            List<String> points,
            boolean highlighted,
            int sortOrder
    ) {
        Experience experience = Experience.create(
                type,
                title,
                organization,
                ExperiencePeriod.of(startDate, endDate),
                points,
                highlighted,
                sortOrder
        );
        Experience saved = experienceRepository.save(experience);
        return ExperienceResult.from(saved);
    }

    public ExperienceResult update(
            Long experienceId,
            ExperienceType type,
            String title,
            String organization,
            LocalDate startDate,
            LocalDate endDate,
            List<String> points,
            boolean highlighted,
            int sortOrder
    ) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ExperienceNotFoundException(experienceId));
        experience.update(
                type,
                title,
                organization,
                ExperiencePeriod.of(startDate, endDate),
                points,
                highlighted,
                sortOrder
        );
        return ExperienceResult.from(experience);
    }

    public void delete(Long experienceId) {
        if (!experienceRepository.existsById(experienceId)) {
            throw new ExperienceNotFoundException(experienceId);
        }
        experienceRepository.deleteById(experienceId);
    }
}
