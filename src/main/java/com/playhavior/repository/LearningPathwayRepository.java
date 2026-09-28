package com.playhavior.repository;

import com.playhavior.entity.LearningPathway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Database access for LearningPathway. Spring Data writes the SQL from method names;
 * save(), findById() and findAll() come free from JpaRepository.
 * USED BY: PlayhaviorWorkflowService (save), LearningPathwayController (find).
 */
public interface LearningPathwayRepository extends JpaRepository<LearningPathway, Long> {

    // SQL: the newest pathway by generated_at. USED BY: LearningPathwayController.showLatestPathway()
    Optional<LearningPathway> findFirstByOrderByGeneratedAtDesc();
}
