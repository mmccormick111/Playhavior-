package com.playhavior.config;

import com.playhavior.entity.Platform;
import com.playhavior.repository.PlatformRepository;
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