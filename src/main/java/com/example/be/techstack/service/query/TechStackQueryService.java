package com.example.be.techstack.service.query;

import com.example.be.techstack.dto.result.TechStackResult;
import com.example.be.techstack.exception.TechStackNotFoundException;
import com.example.be.techstack.repository.TechStackRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechStackQueryService {

    private final TechStackRepository techStackRepository;

    public List<TechStackResult> getAll() {
        return techStackRepository.findAllResults();
    }

    public TechStackResult get(Long techStackId) {
        return techStackRepository.findById(techStackId)
                .map(TechStackResult::from)
                .orElseThrow(() -> new TechStackNotFoundException(techStackId));
    }
}
