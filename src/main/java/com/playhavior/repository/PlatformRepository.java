package com.playhavior.repository;

import com.playhavior.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlatformRepository
        extends JpaRepository<Platform, Long> {

    Optional<Platform> findByPlatformKey(String platformKey);
    boolean existsByPlatformKey(String platformKey);
    List<Platform> findByActiveTrueOrderByDisplayNameAsc();
}
