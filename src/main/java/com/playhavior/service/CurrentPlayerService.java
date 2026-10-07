package com.playhavior.service;

import com.playhavior.entity.Player;
import com.playhavior.repository.PlayerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Answers "which player is using the app right now?" (service layer).
 *
 * CALLED BY: ViolationIntakeController.submitForm() (FLOW step 3).
 * TEMPORARY: returns the demo player seeded by ReferenceDataConfiguration.
 * WHY a separate class: it is the ONE place to change when login exists;
 *     every controller that asks for the current player keeps working.
 * TODO: return the signed-in player once Spring Security login is built.
 */
@Service
public class CurrentPlayerService {

    private final PlayerProfileRepository playerRepository;

    public CurrentPlayerService(
            PlayerProfileRepository playerRepository
    ) {
        this.playerRepository = playerRepository;
    }

    public Player requireCurrentPlayer() {
        return playerRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No player exists. Add a development "
                                        + "player before submitting a notice."
                        )
                );
    }

    // Same lookup, but empty instead of an error (used for the nav-bar avatar)
    public Optional<Player> findCurrentPlayer() {
        return playerRepository.findAll()
                .stream()
                .findFirst();
    }
}
