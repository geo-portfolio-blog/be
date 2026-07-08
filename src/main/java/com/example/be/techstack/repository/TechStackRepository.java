package com.example.be.techstack.repository;

import com.example.be.techstack.domain.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackRepository extends JpaRepository<TechStack, Long>, TechStackRepositoryCustom {
}
