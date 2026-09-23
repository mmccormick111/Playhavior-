package com.playhavior.entity;

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

@Entity
@Table(name = "policy_rules")
public class PolicyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private PlatformPolicy platformPolicy;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "violation_category",
            nullable = false,
            length = 100
    )
    private ViolationCategory violationCategory;

    @Column(name = "section_title", nullable = false, length = 200)
    private String sectionTitle;

    @Column(name = "section_reference", length = 100)
    private String sectionReference;

    @Column(
            name = "rule_summary",
            nullable = false,
            length = 2000
    )
    private String ruleSummary;

    public PolicyRule() {
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public PlatformPolicy getPlatformPolicy() {
        return platformPolicy;
    }

    public void setPlatformPolicy(PlatformPolicy platformPolicy) {
        this.platformPolicy = platformPolicy;
    }

    public ViolationCategory getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(ViolationCategory violationCategory) {
        this.violationCategory = violationCategory;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionReference() {
        return sectionReference;
    }

    public void setSectionReference(String sectionReference) {
        this.sectionReference = sectionReference;
    }

    public String getRuleSummary() {
        return ruleSummary;
    }

    public void setRuleSummary(String ruleSummary) {
        this.ruleSummary = ruleSummary;
    }
}
