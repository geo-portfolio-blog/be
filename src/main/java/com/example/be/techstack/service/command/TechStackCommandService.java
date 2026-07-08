package com.example.be.techstack.service.command;

import com.example.be.techstack.domain.TechStack;
import com.example.be.techstack.domain.TechStackCategory;
import com.example.be.techstack.dto.result.TechStackResult;
import com.example.be.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TechStackCommandService {

    private final TechStackRepository techStackRepository;

    public TechStackResult create(String name, TechStackCategory category, String imageUrl, int proficiency) {
        TechStack techStack = TechStack.create(name, category, imageUrl, proficiency);
        TechStack saved = techStackRepository.save(techStack);
        return TechStackResult.from(saved);
    }
}
