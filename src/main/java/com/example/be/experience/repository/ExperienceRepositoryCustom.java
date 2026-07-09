package com.example.be.experience.repository;

import com.example.be.experience.dto.result.ExperienceResult;
import java.util.List;

public interface ExperienceRepositoryCustom {

    List<ExperienceResult> findAllResults();
}
