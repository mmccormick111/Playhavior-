package com.playhavior.repository;

import com.playhavior.entity.LearningPathway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearningPathwayRepository extends JpaRepository<LearningPathway, Long> {

    Optional<LearningPathway> findFirstByOrderByGeneratedAtDesc();
}
