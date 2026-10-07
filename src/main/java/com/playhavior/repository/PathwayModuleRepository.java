package com.playhavior.repository;

import com.playhavior.entity.PathwayModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Database access for PathwayModule. Modules are SAVED through LearningPathway's
 * cascade; this repository is for FINDING one module.
 * USED BY: ModuleProgressService.
 */
public interface PathwayModuleRepository extends JpaRepository<PathwayModule, Long> {

    // The module with this id, but only if it belongs to this pathway.
    // WHY both ids: an edited URL cannot reach a module from someone else's pathway.
    Optional<PathwayModule> findByModuleIdAndPathway_PathwayId(Long moduleId, Long pathwayId);
}
