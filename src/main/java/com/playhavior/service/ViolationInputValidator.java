package com.playhavior.service;

import com.playhavior.model.PenaltyType;
import com.playhavior.web.form.ViolationInputForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * Checks the rules that involve MORE THAN ONE field (service layer).
 *
 * FLOW: step 3.  CALLED BY: ViolationIntakeController.submitForm()
 * CALLS: CategoryMappingService.isRecognizedReason()
 * WHY not only annotations: @NotNull can say "required",
 *     but not "required IF the ban is temporary".
 * Q: What if someone bypasses the JavaScript? These rules run on the server anyway.
 * NOTE: @Component and @Service both create a bean; @Component is the general form.
 */
@Component
public class ViolationInputValidator {

    private final CategoryMappingService categoryMappingService;

    public ViolationInputValidator(
            CategoryMappingService categoryMappingService
    ) {
        this.categoryMappingService = categoryMappingService;
    }

    // Runs all three checks. Errors is the controller's BindingResult, so these
    // messages show on the page exactly like the annotation errors.
    public void validate(
            ViolationInputForm form,
            Errors errors
    ) {
        validatePenaltyDuration(form, errors);
        validateViolationReason(form, errors);
        validateEvidence(form, errors);
    }

    // Temporary ban / suspension / communication restriction -> length + unit required.
    // Anything else -> both cleared, so a permanent ban never stores a stray "3 days".
    private void validatePenaltyDuration(
            ViolationInputForm form,
            Errors errors
    ) {
        boolean durationRequired =
                form.getPenaltyType() == PenaltyType.TEMPORARY_BAN
                        || form.getPenaltyType() == PenaltyType.ACCOUNT_SUSPENSION
                        || form.getPenaltyType() == PenaltyType.COMMUNICATION_RESTRICTION;

        if (!durationRequired) {
            form.setPenaltyDurationAmount(null);
            form.setPenaltyDurationUnit(null);
            return;
        }

        // rejectValue attaches a message to one field; th:errors shows it under that input
        if (form.getPenaltyDurationAmount() == null) {
            errors.rejectValue(
                    "penaltyDurationAmount",
                    "penaltyDurationAmount.required",
                    "Enter the length of the temporary penalty."
            );
        }

        if (form.getPenaltyDurationUnit() == null) {
            errors.rejectValue(
                    "penaltyDurationUnit",
                    "penaltyDurationUnit.required",
                    "Select hours, days, or weeks."
            );
        }
    }

    // Unknown reason key -> error (catches tampered dropdown values).
    // "Other" without the platform's own wording -> error.
    private void validateViolationReason(
            ViolationInputForm form,
            Errors errors
    ) {
        String reasonKey = form.getViolationReasonKey();

        if (reasonKey == null || reasonKey.isBlank()) {
            return;
        }

        if (!categoryMappingService.isRecognizedReason(reasonKey)) {
            errors.rejectValue(
                    "violationReasonKey",
                    "violationReasonKey.invalid",
                    "Select a valid violation reason."
            );

            return;
        }

        if ("OTHER_PLATFORM_SPECIFIC".equals(reasonKey)
                && isBlank(form.getCustomStatedReason())) {

            errors.rejectValue(
                    "customStatedReason",
                    "customStatedReason.required",
                    "Enter the reason shown by the platform."
            );
        }
    }

    // "Yes" with no evidence text -> error. "No" -> any text is cleared.
    private void validateEvidence(
            ViolationInputForm form,
            Errors errors
    ) {
        if (Boolean.TRUE.equals(
                form.getPlatformProvidedEvidence()
        ) && isBlank(form.getEvidenceText())) {

            errors.rejectValue(
                    "evidenceText",
                    "evidenceText.required",
                    "Enter the evidence provided in the notice."
            );
        }

        if (Boolean.FALSE.equals(
                form.getPlatformProvidedEvidence()
        )) {
            form.setEvidenceText(null);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
