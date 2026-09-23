package com.playhavior.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.playhavior.model.DurationUnit;
import com.playhavior.model.PenaltyType;
import com.playhavior.model.ViolationCategory;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ban_reports")
public class BanReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long report_id;

    private String stated_reason;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;


    public BanReport() {
    }

    public Long getReport_id(){
        return report_id;
    }

    public String getStated_reason(){
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

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_type", nullable = false)
    private PenaltyType penaltyType;

    @Column(name = "penalty_duration_amount")
    private Integer penaltyDurationAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_duration_unit")
    private DurationUnit penaltyDurationUnit;

    @Column(name = "violation_reason_key", nullable = false)
    private String violationReasonKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_category", nullable = false)
    private ViolationCategory violationCategory;

    @Column(name = "platform_provided_evidence", nullable = false)
    private boolean platformProvidedEvidence;

    @Column(name = "evidence_text", length = 5000)
    private String evidenceText;

    @Column(name = "ban_issue_date", nullable = false)
    private LocalDate banIssueDate;

    @Column(name = "platform_case_number", length = 100)
    private String platformCaseNumber;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
}
