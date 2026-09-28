package com.playhavior.repository;

import com.playhavior.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Database access for Platform. WHY Optional: "not found" must be handled by the caller. */
public interface PlatformRepository
        extends JpaRepository<Platform, Long> {

    // WHERE platform_key = ?  USED BY: ReferenceDataConfiguration, CSV importer, workflow service
    Optional<Platform> findByPlatformKey(String platformKey);
    // true/false instead of a row (not used yet)
    boolean existsByPlatformKey(String platformKey);
    // Active platforms, A to Z. USED BY: the Platform dropdown (ViolationIntakeController)
    List<Platform> findByActiveTrueOrderByDisplayNameAsc();
}
