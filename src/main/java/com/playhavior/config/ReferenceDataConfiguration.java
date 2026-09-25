package com.playhavior.config;

import com.playhavior.entity.Platform;
import com.playhavior.entity.Player;
import com.playhavior.repository.PlatformRepository;
import com.playhavior.repository.PlayerProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ReferenceDataConfiguration {

    @Bean
    @Order(1)
    CommandLineRunner loadSupportedPlatforms(
            PlatformRepository platformRepository
    ) {
        return args -> {
            addPlatform(
                    platformRepository,
                    "STEAM",
                    "Steam"
            );

            addPlatform(
                    platformRepository,
                    "PLAYSTATION_NETWORK",
                    "PlayStation Network"
            );

            addPlatform(
                    platformRepository,
                    "XBOX",
                    "Xbox"
            );

            addPlatform(
                    platformRepository,
                    "EPIC_GAMES",
                    "Epic Games"
            );
        };
    }

    /*
     * DEMO ONLY: until sign-up/login is built, every notice is
     * submitted as this player (see CurrentPlayerService).
     */
    @Bean
    @Order(1)
    CommandLineRunner loadDemoPlayer(
            PlayerProfileRepository playerRepository
    ) {
        return args -> {
            if (playerRepository.count() > 0) {
                return;
            }

            Player player = new Player();
            player.setDisplay_name("Jordan Lee");
            player.setEmail("demo.player@playhavior.local");
            player.setPassword("demo-only-not-a-real-password");

            playerRepository.save(player);
        };
    }

    private void addPlatform(
            PlatformRepository repository,
            String key,
            String displayName
    ) {
        if (repository.findByPlatformKey(key).isPresent()) {
            return;
        }

        Platform platform = new Platform();
        platform.setPlatformKey(key);
        platform.setDisplayName(displayName);
        platform.setActive(true);

        repository.save(platform);
    }
}