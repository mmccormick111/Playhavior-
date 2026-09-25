package com.playhavior.web.form;

import com.playhavior.model.DurationUnit;
import com.playhavior.model.PenaltyType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ViolationInputForm {

    @AssertTrue(message =
            "You must confirm that the information is accurate.")
    private boolean accuracyAcknowledged;

    @AssertTrue(message =
            "You must acknowledge that Playhavior cannot guarantee reinstatement.")
    private boolean limitationAcknowledged;

    @NotBlank(message = "Select the platform that issued the notice.")
    private String platformKey;

    @Size(max = 150)
    private String gameTitle;

    @NotNull(message = "Select the type of penalty.")
    private PenaltyType penaltyType;

    @Positive(message = "Enter a duration greater than zero.")
    private Integer penaltyDurationAmount;

    private DurationUnit penaltyDurationUnit;

    @NotBlank(message =
            "Select the closest reason shown in the notice.")
    private String violationReasonKey;

    @Size(max = 500)
    private String customStatedReason;

    @NotNull(message =
            "Select whether the platform provided evidence.")
    private Boolean platformProvidedEvidence;

    @Size(max = 5000)
    private String evidenceText;

    @NotNull(message = "Enter the notice issue date.")
    @PastOrPresent(message =
            "The notice issue date cannot be in the future.")
    private LocalDate banIssueDate;

    @Size(max = 100)
    private String platformCaseNumber;



    public boolean isAccuracyAcknowledged() {
        return accuracyAcknowledged;
    }

    public void setAccuracyAcknowledged(boolean accuracyAcknowledged) {
        this.accuracyAcknowledged = accuracyAcknowledged;
    }

    public boolean isLimitationAcknowledged() {
        return limitationAcknowledged;
    }

    public void setLimitationAcknowledged(boolean limitationAcknowledged) {
        this.limitationAcknowledged = limitationAcknowledged;
    }

    public String getPlatformKey() {
        return platformKey;
    }

    public void setPlatformKey(String platformKey) {
        this.platformKey = platformKey;
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

    public void setPenaltyDurationAmount(Integer penaltyDurationAmount) {
        this.penaltyDurationAmount = penaltyDurationAmount;
    }

    public DurationUnit getPenaltyDurationUnit() {
        return penaltyDurationUnit;
    }

    public void setPenaltyDurationUnit(DurationUnit penaltyDurationUnit) {
        this.penaltyDurationUnit = penaltyDurationUnit;
    }

    public String getViolationReasonKey() {
        return violationReasonKey;
    }

    public void setViolationReasonKey(String violationReasonKey) {
        this.violationReasonKey = violationReasonKey;
    }

    public String getCustomStatedReason() {
        return customStatedReason;
    }

    public void setCustomStatedReason(String customStatedReason) {
        this.customStatedReason = customStatedReason;
    }

    public Boolean getPlatformProvidedEvidence() {
        return platformProvidedEvidence;
    }

    public void setPlatformProvidedEvidence(Boolean platformProvidedEvidence) {
        this.platformProvidedEvidence = platformProvidedEvidence;
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

    public void setBanIssueDate(LocalDate banIssueDate) {
        this.banIssueDate = banIssueDate;
    }

    public String getPlatformCaseNumber() {
        return platformCaseNumber;
    }

    public void setPlatformCaseNumber(String platformCaseNumber) {
        this.platformCaseNumber = platformCaseNumber;
    }

    public ViolationInputForm() {

    }

}
