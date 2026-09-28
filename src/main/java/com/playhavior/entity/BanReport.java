package com.playhavior.entity;

import com.playhavior.model.DurationUnit;
import com.playhavior.model.PenaltyType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Everything the player entered about their violation notice (table: ban_reports).
 * This is the row to check in the H2 console after a demo submission.
 *
 * RELATIONSHIPS: many reports -> one Platform (@ManyToOne, platform_id).
 *                One PlayerCase points to each report (report_id).
 * CREATED BY: PlayhaviorWorkflowService.buildBanReport() (FLOW step 5)
 * NOTE: stated_reason / report_id use older snake_case names; newer entities
 *       use camelCase fields with @Column(name = ...).
 */
@Entity
@Table(name = "ban_reports")
public class BanReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long report_id;

    // The readable reason ("Personal insults"), or the player's own words for "Other"
    @Column(name = "stated_reason", nullable = false)
    private String stated_reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(name = "game_title", length = 150)
    private String gameTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_type", nullable = false)
    private PenaltyType penaltyType;

    // WHY Integer, not int: null = no duration (e.g. a permanent ban)
    @Column(name = "penalty_duration_amount")
    private Integer penaltyDurationAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_duration_unit")
    private DurationUnit penaltyDurationUnit;

    // WHY both key AND category: the key keeps the exact detail the player picked;
    // the category is the decision recorded at submission time (used for policy lookup)
    @Column(name = "violation_reason_key", nullable = false)
    private String violationReasonKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_category", nullable = false)
    private ViolationCategory violationCategory;

    @Column(name = "platform_provided_evidence", nullable = false)
    private boolean platformProvidedEvidence;

    @Column(name = "evidence_text", length = 5000)
    private String evidenceText;

    // LocalDate: the notice only has a date. submittedAt below is a full timestamp.
    @Column(name = "ban_issue_date", nullable = false)
    private LocalDate banIssueDate;

    // The platform's own reference code; goes on the summary report so the
    // platform can look up the case during an appeal
    @Column(name = "platform_case_number", length = 100)
    private String platformCaseNumber;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    // Required by Hibernate: it creates an empty object, then fills it from the row
    public BanReport() {
    }

    public Long getReport_id() {
        return report_id;
    }

    public String getStated_reason() {
        return stated_reason;
    }

    public void setStated_reason(String stated_reason) {
        this.stated_reason = stated_reason;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public PenaltyType getPenaltyType() {
        return penaltyType;
    }

    public void setPenaltyType(PenaltyType penaltyType) {
        this.penaltyType = penaltyType;
    }

    public Integer getPenaltyDurationAmount() {
        return penaltyDurationAmount;
    }

    public void setPenaltyDurationAmount(
            Integer penaltyDurationAmount
    ) {
        this.penaltyDurationAmount = penaltyDurationAmount;
    }

    public DurationUnit getPenaltyDurationUnit() {
        return penaltyDurationUnit;
    }

    public void setPenaltyDurationUnit(
            DurationUnit penaltyDurationUnit
    ) {
        this.penaltyDurationUnit = penaltyDurationUnit;
    }

    public String getViolationReasonKey() {
        return violationReasonKey;
    }

    public void setViolationReasonKey(
            String violationReasonKey
    ) {
        this.violationReasonKey = violationReasonKey;
    }

    public ViolationCategory getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(
            ViolationCategory violationCategory
    ) {
        this.violationCategory = violationCategory;
    }

    public boolean isPlatformProvidedEvidence() {
        return platformProvidedEvidence;
    }

    public void setPlatformProvidedEvidence(
            boolean platformProvidedEvidence
    ) {
        this.platformProvidedEvidence =
                platformProvidedEvidence;
    }

    public String getEvidenceText() {
        return evidenceText;
    }

    public void setEvidenceText(String evidenceText) {
        this.evidenceText = evidenceText;
    }

    public LocalDate getBanIssueDate() {
        return banIssueDate;
    }

    public void setBanIssueDate(
            LocalDate banIssueDate
    ) {
        this.banIssueDate = banIssueDate;
    }

    public String getPlatformCaseNumber() {
        return platformCaseNumber;
    }

    public void setPlatformCaseNumber(
            String platformCaseNumber
    ) {
        this.platformCaseNumber = platformCaseNumber;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(
            LocalDateTime submittedAt
    ) {
        this.submittedAt = submittedAt;
    }
}