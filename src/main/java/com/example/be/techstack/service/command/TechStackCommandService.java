package com.example.be.techstack.service.command;

import com.example.be.techstack.domain.TechStack;
import com.example.be.techstack.domain.TechStackCategory;
import com.example.be.techstack.dto.result.TechStackResult;
import com.example.be.techstack.exception.TechStackNotFoundException;
import com.example.be.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TechStackCommandService {

    private final TechStackRepository techStackRepository;

    public TechStackResult create(String name, TechStackCategory category, String note, String imageUrl, int sortOrder) {
        TechStack techStack = TechStack.create(name, category, note, imageUrl, sortOrder);
        TechStack saved = techStackRepository.save(techStack);
        return TechStackResult.from(saved);
    }

    public TechStackResult update(
            Long techStackId,
            String name,
            TechStackCategory category,
            String note,
            String imageUrl,
            int sortOrder
    ) {
        TechStack techStack = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));
        techStack.update(name, category, note, imageUrl, sortOrder);
        return TechStackResult.from(techStack);
    }

    public void delete(Long techStackId) {
        if (!techStackRepository.existsById(techStackId)) {
            throw new TechStackNotFoundException(techStackId);
        }
        techStackRepository.deleteById(techStackId);
    }
}
