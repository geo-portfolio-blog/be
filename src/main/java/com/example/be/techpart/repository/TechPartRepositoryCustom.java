package com.example.be.techpart.repository;

import com.example.be.techpart.dto.result.TechPartResult;
import java.util.List;

public interface TechPartRepositoryCustom {

    List<TechPartResult> findAllResults();
}
