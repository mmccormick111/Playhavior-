package com.playhavior.repository;

import com.playhavior.entity.PlatformPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import com.playhavior.entity.Platform;
import java.util.Optional;

/** Database access for PlatformPolicy. */
public interface PlatformPolicyRepository
        extends JpaRepository<PlatformPolicy, Long> {

    // The policy matching all three. USED BY: CSV importer, to avoid creating duplicates
    Optional<PlatformPolicy>
    findFirstByPlatformAndTitleAndVersionLabel(
            Platform platform,
            String title,
            String versionLabel
    );

    // Newest active policy for a platform key. The _ walks into the linked Platform's
    // platformKey field. USED BY: PlatformPolicyService.findActivePolicy()
    Optional<PlatformPolicy>
    findFirstByPlatform_PlatformKeyAndActiveTrueOrderByEffectiveDateDesc(
            String platformKey
    );
}
