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

/**
 * Holds exactly what the notice form submits. NOT an entity; never saved directly.
 *
 * FLOW: filled by Spring in step 3 (data binding: input name -> setter);
 *       read by PlayhaviorWorkflowService in steps 4-5.
 * WHY a separate class instead of binding straight to BanReport:
 *   - it has fields that are not stored (the two acknowledgement checkboxes)
 *   - it holds keys the user picks (platformKey) where the entity holds objects (Platform)
 *   - it stops users posting fields they should not set, such as ids (mass assignment)
 * VALIDATION: the annotations check single fields (@Valid in the controller);
 *             ViolationInputValidator checks rules between fields.
 */
public class ViolationInputForm {

    // ===== FIELDS + VALIDATION (each message shows under its input) =====

    // The two checkboxes: @AssertTrue = must be ticked
    @AssertTrue(message =
            "You must confirm that the information is accurate.")
    private boolean accuracyAcknowledged;

    @AssertTrue(message =
            "You must acknowledge that Playhavior cannot guarantee reinstatement.")
    private boolean limitationAcknowledged;

    // @NotBlank: a String must contain text. Holds a key such as "XBOX".
    @NotBlank(message = "Select the platform that issued the notice.")
    private String platformKey;

    // Optional. WHY @Size: matches the database column length, so a long entry gets
    // a friendly message instead of a database error.
    @Size(max = 150)
    private String gameTitle;

    // @NotNull: an object (here an enum) must be chosen. Spring converts the
    // submitted text "TEMPORARY_BAN" into PenaltyType.TEMPORARY_BAN.
    @NotNull(message = "Select the type of penalty.")
    private PenaltyType penaltyType;

    // Duration: only required for temporary penalties.
    // WHY no @NotNull here: that "only if" rule lives in ViolationInputValidator.
    @Positive(message = "Enter a duration greater than zero.")
    private Integer penaltyDurationAmount;

    private DurationUnit penaltyDurationUnit;

    // A reason key such as "PERSONAL_INSULTS"; must exist in CategoryMappingService
    @NotBlank(message =
            "Select the closest reason shown in the notice.")
    private String violationReasonKey;

    // Only used when the reason is OTHER_PLATFORM_SPECIFIC
    @Size(max = 500)
    private String customStatedReason;

    // WHY Boolean, not boolean: null means "not answered", which @NotNull catches.
    // A boolean would default to false and look like "No".
    @NotNull(message =
            "Select whether the platform provided evidence.")
    private Boolean platformProvidedEvidence;

    @Size(max = 5000)
    private String evidenceText;

    // @PastOrPresent: a notice cannot be dated in the future
    @NotNull(message = "Enter the notice issue date.")
    @PastOrPresent(message =
            "The notice issue date cannot be in the future.")
    private LocalDate banIssueDate;

    // Optional platform reference code; goes on the summary report for appeals
    @Size(max = 100)
    private String platformCaseNumber;



    // ===== GETTERS / SETTERS =====
    // WHY: Spring data binding calls the setters; th:field calls the getters.

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

    // Empty constructor: Spring creates an empty form, then fills it
    public ViolationInputForm() {

    }

}
