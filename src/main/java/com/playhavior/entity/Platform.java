package com.playhavior.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "platforms")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long platformId;

    @Column(
            name = "platform_key",
            nullable = false,
            unique = true,
            length = 50
    )
    private String platformKey;

    @Column(
            name = "display_name",
            nullable = false,
            unique = true,
            length = 100
    )
    private String displayName;

    @Column(nullable = false)
    private boolean active = true;

    public Platform() {
    }

    public Platform(
            String platformKey,
            String displayName
    ) {
        this.platformKey = platformKey;
        this.displayName = displayName;
        this.active = true;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getPlatformKey() {
        return platformKey;
    }

    public void setPlatformKey(String platformKey) {
        this.platformKey = platformKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
