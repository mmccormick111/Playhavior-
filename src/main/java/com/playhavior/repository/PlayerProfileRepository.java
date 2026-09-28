package com.playhavior.repository;

import com.playhavior.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access for the Player entity (the name says "Profile"; the entity is Player).
 * USED BY: ReferenceDataConfiguration (demo player), CurrentPlayerService.
 */
public interface PlayerProfileRepository extends JpaRepository<Player, Long> {
}
