package com.example.be.techpart.repository;

import com.example.be.techpart.domain.TechPart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechPartRepository extends JpaRepository<TechPart, Long>, TechPartRepositoryCustom {
}
