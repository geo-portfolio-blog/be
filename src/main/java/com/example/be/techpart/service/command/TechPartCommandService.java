package com.example.be.techpart.service.command;

import com.example.be.techpart.domain.TechPart;
import com.example.be.techpart.domain.TechSkill;
import com.example.be.techpart.dto.result.TechPartResult;
import com.example.be.techpart.exception.TechPartNotFoundException;
import com.example.be.techpart.repository.TechPartRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TechPartCommandService {

    private final TechPartRepository techPartRepository;

    public TechPartResult create(String name, int sortOrder, List<TechSkillCommand> skills) {
        TechPart part = TechPart.create(name, sortOrder, toSkills(skills));
        TechPart saved = techPartRepository.save(part);
        return TechPartResult.from(saved);
    }

    public TechPartResult update(Long techPartId, String name, int sortOrder, List<TechSkillCommand> skills) {
        TechPart part = techPartRepository.findById(techPartId)
                .orElseThrow(() -> new TechPartNotFoundException(techPartId));
        part.update(name, sortOrder, toSkills(skills));
        return TechPartResult.from(part);
    }

    public void delete(Long techPartId) {
        if (!techPartRepository.existsById(techPartId)) {
            throw new TechPartNotFoundException(techPartId);
        }
        techPartRepository.deleteById(techPartId);
    }

    private List<TechSkill> toSkills(List<TechSkillCommand> skills) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream()
                .map(skill -> TechSkill.of(skill.name(), skill.note()))
                .toList();
    }
}
