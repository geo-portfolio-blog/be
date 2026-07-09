package com.example.be.experience.repository;

import com.example.be.experience.domain.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long>, ExperienceRepositoryCustom {
}
