package com.playhavior.entity;

import com.playhavior.model.ModuleStatus;
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

/*
 * One module inside a generated learning pathway.
 * Titles and lesson counts are placeholders for now; lesson
 * content and the closing scenario will be added later.
 *
 * TABLE: pathway_modules.
 * RELATIONSHIP: many modules -> one LearningPathway (@ManyToOne, owns pathway_id).
 * CREATED BY: ModulePlanService.buildPlan(); saved through LearningPathway's cascade.
 */
@Entity
@Table(name = "pathway_modules")
public class PathwayModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long moduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pathway_id", nullable = false)
    private LearningPathway pathway;

    @Column(name = "module_order", nullable = false)
    private int moduleOrder;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "lesson_count", nullable = false)
    private int lessonCount;

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes;

    // Drives the icon on the page: number (AVAILABLE), lock (LOCKED), tick (COMPLETED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModuleStatus status;

    // Required by Hibernate: it creates an empty object, then fills it from the row
    public PathwayModule() {
    }

    // Convenience constructor so ModulePlanService can build a module in one line
    public PathwayModule(
            int moduleOrder,
            String title,
            String description,
            int lessonCount,
            int estimatedMinutes,
            ModuleStatus status
    ) {
        this.moduleOrder = moduleOrder;
        this.title = title;
        this.description = description;
        this.lessonCount = lessonCount;
        this.estimatedMinutes = estimatedMinutes;
        this.status = status;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public LearningPathway getPathway() {
        return pathway;
    }

    public void setPathway(LearningPathway pathway) {
        this.pathway = pathway;
    }

    public int getModuleOrder() {
        return moduleOrder;
    }

    public void setModuleOrder(int moduleOrder) {
        this.moduleOrder = moduleOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public ModuleStatus getStatus() {
        return status;
    }

    public void setStatus(ModuleStatus status) {
        this.status = status;
    }
}
