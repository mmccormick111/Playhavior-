package com.playhavior.repository;

import com.playhavior.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerProfileRepository extends JpaRepository<Player, Long> {
}
