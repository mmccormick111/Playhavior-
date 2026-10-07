package com.playhavior.entity;

import com.playhavior.model.ModuleStatus;
import com.playhavior.model.ModuleType;
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

import java.time.LocalDateTime;

/*
 * One interactive module inside a generated learning pathway.
 * Its content (scenarios, choices) lives in ModuleContentCatalog, looked up by
 * moduleType + the pathway's track; this row stores progress and telemetry.
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

    // Number of interactive steps (decisions, scenarios) in the module.
    // (Column kept as lesson_count from the first version.)
    @Column(name = "lesson_count", nullable = false)
    private int lessonCount;

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes;

    // Drives the icon on the page: number (AVAILABLE), lock (LOCKED), tick (COMPLETED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModuleStatus status;

    // Which kind of module this is: picks the page template and the grading rules
    @Enumerated(EnumType.STRING)
    @Column(name = "module_type", nullable = false, length = 40)
    private ModuleType moduleType;

    // ===== PROGRESS + TELEMETRY (filled in by ModuleProgressService) =====
    // These become the "Application Decision Dashboard" metrics in the appeal packet.

    // Set the first time the player opens the module page
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // completedAt - startedAt: feeds "Reading/Engagement Velocity"
    @Column(name = "seconds_spent", nullable = false)
    private long secondsSpent;

    // Graded decisions in this module, and how many were right on the first try:
    // feeds "First-Pass Accuracy"
    @Column(name = "question_count", nullable = false)
    private int questionCount;

    @Column(name = "first_pass_correct", nullable = false)
    private int firstPassCorrect;

    // Every attempt, including retries after a wrong choice
    @Column(name = "total_attempts", nullable = false)
    private int totalAttempts;

    // Earned in the Accountability Sandbox (framework Phase 3)
    @Column(name = "standing_points", nullable = false)
    private int standingPoints;

    // Required by Hibernate: it creates an empty object, then fills it from the row
    public PathwayModule() {
    }

    // Convenience constructor so ModulePlanService can build a module in one line
    public PathwayModule(
            ModuleType moduleType,
            int moduleOrder,
            String title,
            String description,
            int lessonCount,
            int estimatedMinutes,
            ModuleStatus status
    ) {
        this.moduleType = moduleType;
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

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public long getSecondsSpent() {
        return secondsSpent;
    }

    public void setSecondsSpent(long secondsSpent) {
        this.secondsSpent = secondsSpent;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getFirstPassCorrect() {
        return firstPassCorrect;
    }

    public void setFirstPassCorrect(int firstPassCorrect) {
        this.firstPassCorrect = firstPassCorrect;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public int getStandingPoints() {
        return standingPoints;
    }

    public void setStandingPoints(int standingPoints) {
        this.standingPoints = standingPoints;
    }
}
