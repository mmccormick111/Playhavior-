package com.playhavior.service;

import com.playhavior.entity.Player;
import com.playhavior.repository.PlayerProfileRepository;
import org.springframework.stereotype.Service;

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
}
