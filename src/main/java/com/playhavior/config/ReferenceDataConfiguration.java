package com.playhavior.config;

import com.playhavior.entity.Platform;
import com.playhavior.entity.Player;
import com.playhavior.repository.PlatformRepository;
import com.playhavior.repository.PlayerProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Puts starting data into the empty database at startup (config).
 *
 * FLOW: step 0. Runs once, before any request.
 * ORDER: @Order(1) here, then PlatformPolicyCsvImporter @Order(2),
 *        because every CSV row names a platform that must already exist.
 * WHY CommandLineRunner beans: Spring runs each one right after startup.
 */
@Configuration
public class ReferenceDataConfiguration {

    // The 4 supported platforms. platformKey = stable code for the program and CSV;
    // displayName = what people see in the dropdown.
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
            // TODO: remove this seed (and hash passwords) once sign-up/login exists
            player.setPassword("demo-only-not-a-real-password");

            playerRepository.save(player);
        };
    }

    private void addPlatform(
            PlatformRepository repository,
            String key,
            String displayName
    ) {
        // WHY: idempotent (safe to run twice); matters once the database keeps data
        //      between runs, where a duplicate key would break the unique constraint
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