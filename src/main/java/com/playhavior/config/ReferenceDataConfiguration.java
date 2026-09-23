package com.playhavior.config;

import com.playhavior.entity.Platform;
import com.playhavior.repository.PlatformRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReferenceDataConfiguration {

    @Bean
    CommandLineRunner loadPlatformReferenceData(
            PlatformRepository platformRepository
    ) {
        return args -> {
            createPlatformIfMissing(
                    platformRepository,
                    "STEAM",
                    "Steam"
            );

            createPlatformIfMissing(
                    platformRepository,
                    "PLAYSTATION_NETWORK",
                    "PlayStation Network"
            );

            createPlatformIfMissing(
                    platformRepository,
                    "XBOX",
                    "Xbox"
            );

            createPlatformIfMissing(
                    platformRepository,
                    "EPIC_GAMES",
                    "Epic Games"
            );
        };
    }

    private void createPlatformIfMissing(
            PlatformRepository repository,
            String platformKey,
            String displayName
    ) {
        if (repository.existsByPlatformKey(platformKey)) {
            return;
        }

        Platform platform = new Platform();
        platform.setPlatformKey(platformKey);
        platform.setDisplayName(displayName);
        platform.setActive(true);

        repository.save(platform);
    }
}
