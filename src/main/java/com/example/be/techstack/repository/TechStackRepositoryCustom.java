package com.example.be.techstack.repository;

import com.example.be.techstack.dto.result.TechStackResult;
import java.util.List;

public interface TechStackRepositoryCustom {

    List<TechStackResult> findAllResults();
}
