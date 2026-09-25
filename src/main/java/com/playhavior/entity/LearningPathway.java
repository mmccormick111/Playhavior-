package com.playhavior.entity;

import com.playhavior.model.ModuleStatus;
import com.playhavior.model.PathwayCode;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.PersonalizationLevel;
import com.playhavior.model.ViolationCategory;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_pathways")
public class LearningPathway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pathway_id")
    private Long pathwayId;

    @Column(name = "total_modules", nullable = false)
    private int totalModules;

    @Column(name = "pathway_title", nullable = false)
    private String pathwayTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_category", nullable = false)
    private ViolationCategory violationCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "pathway_code", nullable = false)
    private PathwayCode pathwayCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "pathway_mode", nullable = false)
    private PathwayMode pathwayMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "personalization_level", nullable = false)
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
    private PlayerCase playerCase;

    @OneToMany(
            mappedBy = "pathway",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("moduleOrder ASC")
    private List<PathwayModule> modules = new ArrayList<>();

    public LearningPathway() {
    }

    /*
     * Keeps both sides of the relationship in sync
     * and updates the stored module count.
     */
    public void addModule(PathwayModule module) {
        module.setPathway(this);
        modules.add(module);
        totalModules = modules.size();
    }

    public int getCompletedModules() {
        return (int) modules.stream()
                .filter(module -> module.getStatus() == ModuleStatus.COMPLETED)
                .count();
    }

    public int getProgressPercent() {
        return totalModules == 0
                ? 0
                : getCompletedModules() * 100 / totalModules;
    }

    public int getTotalLessons() {
        return modules.stream()
                .mapToInt(PathwayModule::getLessonCount)
                .sum();
    }

    public int getTotalMinutes() {
        return modules.stream()
                .mapToInt(PathwayModule::getEstimatedMinutes)
                .sum();
    }

    public Long getPathwayId() {
        return pathwayId;
    }

    public int getTotalModules() {
        return totalModules;
    }

    public String getPathwayTitle() {
        return pathwayTitle;
    }

    public void setPathwayTitle(String pathwayTitle) {
        this.pathwayTitle = pathwayTitle;
    }

    public ViolationCategory getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(ViolationCategory violationCategory) {
        this.violationCategory = violationCategory;
    }

    public PathwayCode getPathwayCode() {
        return pathwayCode;
    }

    public void setPathwayCode(PathwayCode pathwayCode) {
        this.pathwayCode = pathwayCode;
    }

    public PathwayMode getPathwayMode() {
        return pathwayMode;
    }

    public void setPathwayMode(PathwayMode pathwayMode) {
        this.pathwayMode = pathwayMode;
    }

    public PersonalizationLevel getPersonalizationLevel() {
        return personalizationLevel;
    }

    public void setPersonalizationLevel(PersonalizationLevel personalizationLevel) {
        this.personalizationLevel = personalizationLevel;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public PlatformPolicy getPlatformPolicy() {
        return platformPolicy;
    }

    public void setPlatformPolicy(PlatformPolicy platformPolicy) {
        this.platformPolicy = platformPolicy;
    }

    public PlayerCase getPlayerCase() {
        return playerCase;
    }

    public void setPlayerCase(PlayerCase playerCase) {
        this.playerCase = playerCase;
    }

    public List<PathwayModule> getModules() {
        return modules;
    }
}
