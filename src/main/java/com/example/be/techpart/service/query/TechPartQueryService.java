package com.example.be.techpart.service.query;

import com.example.be.techpart.dto.result.TechPartResult;
import com.example.be.techpart.exception.TechPartNotFoundException;
import com.example.be.techpart.repository.TechPartRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechPartQueryService {

    private final TechPartRepository techPartRepository;

    public List<TechPartResult> getAll() {
        return techPartRepository.findAllResults();
    }

    public TechPartResult get(Long techPartId) {
        return techPartRepository.findById(techPartId)
                .map(TechPartResult::from)
                .orElseThrow(() -> new TechPartNotFoundException(techPartId));
    }
}
