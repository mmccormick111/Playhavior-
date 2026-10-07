package com.playhavior.repository;

import com.playhavior.entity.Player;
import com.playhavior.entity.PlayerCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Database access for PlayerCase. USED BY: PlayhaviorWorkflowService (save, FLOW step 5). */
public interface PlayerCaseRepository extends JpaRepository<PlayerCase, Long> {

    // All of a player's cases, newest first. USED BY: DashboardService
    List<PlayerCase> findByPlayerOrderByCaseIdDesc(Player player);
}
