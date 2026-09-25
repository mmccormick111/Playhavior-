package com.playhavior.repository;

import com.playhavior.entity.PlatformPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import com.playhavior.entity.Platform;
import java.util.Optional;

public interface PlatformPolicyRepository
        extends JpaRepository<PlatformPolicy, Long> {

    Optional<PlatformPolicy>
    findFirstByPlatformAndTitleAndVersionLabel(
            Platform platform,
            String title,
            String versionLabel
    );

    Optional<PlatformPolicy>
    findFirstByPlatform_PlatformKeyAndActiveTrueOrderByEffectiveDateDesc(
            String platformKey
    );
}
