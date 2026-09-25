package com.playhavior.service;

import com.playhavior.model.PenaltyType;
import com.playhavior.web.form.ViolationInputForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class ViolationInputValidator {

    private final CategoryMappingService categoryMappingService;

    public ViolationInputValidator(
            CategoryMappingService categoryMappingService
    ) {
        this.categoryMappingService = categoryMappingService;
    }

    public void validate(
            ViolationInputForm form,
            Errors errors
    ) {
        validatePenaltyDuration(form, errors);
        validateViolationReason(form, errors);
        validateEvidence(form, errors);
    }

    private void validatePenaltyDuration(
            ViolationInputForm form,
            Errors errors
    ) {
        boolean durationRequired =
                form.getPenaltyType() == PenaltyType.TEMPORARY_BAN
                        || form.getPenaltyType()
                        == PenaltyType.ACCOUNT_SUSPENSION;

        if (!durationRequired) {
            form.setPenaltyDurationAmount(null);
            form.setPenaltyDurationUnit(null);
            return;
        }

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
