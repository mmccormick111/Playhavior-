package com.playhavior.repository;

import com.playhavior.entity.PlatformPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformPolicyRepository
        extends JpaRepository<PlatformPolicy, Long> {

    Optional<PlatformPolicy>
    findFirstByPlatformPlatformKeyAndActiveTrueOrderByEffectiveDateDesc(
            String platformKey
    );
}
