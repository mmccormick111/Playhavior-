package com.playhavior.repository;

import com.playhavior.entity.PlayerCase;
import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for PlayerCase. USED BY: PlayhaviorWorkflowService (save, FLOW step 5). */
public interface PlayerCaseRepository extends JpaRepository<PlayerCase, Long> {
}
