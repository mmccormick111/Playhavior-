package com.playhavior.entity;

import com.playhavior.model.PathwayCode;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.PersonalizationLevel;
import com.playhavior.model.ViolationCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_pathways")
public class LearningPathway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pathway_id;

    @Column(name = "total_modules", nullable = false)
    private int total_modules;

    @Column(name = "pathway_title", nullable = false)
    private String pathway_title;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "violation_category",
            nullable = false
    )
    private ViolationCategory violationCategory;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "pathway_code",
            nullable = false
    )
    private PathwayCode pathwayCode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "pathway_mode",
            nullable = false
    )
    private PathwayMode pathwayMode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "personalization_level",
            nullable = false
    )
    private PersonalizationLevel personalizationLevel;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private PlatformPolicy platformPolicy;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "case_id",
            nullable = false,
            unique = true
    )
    private Case playerCase;

    public LearningPathway() {
    }
    public Case getPlayerCase() {
        return playerCase;
    }

    public void setPlayerCase(Case playerCase) {
        this.playerCase = playerCase;
    }

    public Long getPathway_ID() {
        return pathway_id;
    }

    public int getTotal_modules() {
        return total_modules;
    }

    public void setTotal_modules(int total_modules) {
        this.total_modules = total_modules;
    }

    public String getPathway_title() {
        return pathway_title;
    }

    public void setPathway_title(
            String pathway_title
    ) {
        this.pathway_title = pathway_title;
    }

    public ViolationCategory getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(
            ViolationCategory violationCategory
    ) {
        this.violationCategory = violationCategory;
    }

    public PathwayCode getPathwayCode() {
        return pathwayCode;
    }

    public void setPathwayCode(
            PathwayCode pathwayCode
    ) {
        this.pathwayCode = pathwayCode;
    }

    public PathwayMode getPathwayMode() {
        return pathwayMode;
    }

    public void setPathwayMode(
            PathwayMode pathwayMode
    ) {
        this.pathwayMode = pathwayMode;
    }

    public PersonalizationLevel getPersonalizationLevel() {
        return personalizationLevel;
    }

    public void setPersonalizationLevel(
            PersonalizationLevel personalizationLevel
    ) {
        this.personalizationLevel =
                personalizationLevel;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(
            LocalDateTime generatedAt
    ) {
        this.generatedAt = generatedAt;
    }

    public PlatformPolicy getPlatformPolicy() {
        return platformPolicy;
    }

    public void setPlatformPolicy(
            PlatformPolicy platformPolicy
    ) {
        this.platformPolicy = platformPolicy;
    }
}