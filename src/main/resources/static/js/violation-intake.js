// Shows/hides form fields based on earlier answers (convenience only).
// WHY the server still checks: JS can be disabled; ViolationInputValidator enforces
//     the same rules on the server.
// LOADED BY: violation-intake.html (at the end of <body>, so the HTML exists first)
document.addEventListener("DOMContentLoaded", () => {
    const penaltyType =
        document.getElementById("penaltyType");

    const durationFields =
        document.getElementById("duration-fields");

    const durationAmount =
        document.getElementById("penaltyDurationAmount");

    const durationUnit =
        document.getElementById("penaltyDurationUnit");

    const reason =
        document.getElementById("violationReasonKey");

    const customReasonGroup =
        document.getElementById("custom-reason-group");

    const customReason =
        document.getElementById("customStatedReason");

    const evidenceGroup =
        document.getElementById("evidence-group");

    const evidenceText =
        document.getElementById("evidenceText");

    const evidenceRadios =
        document.querySelectorAll(
            "input[name='platformProvidedEvidence']"
        );

    // Length + unit: shown and required only for temporary penalties
    function updateDuration() {
        const temporaryTypes = [
            "TEMPORARY_BAN",
            "ACCOUNT_SUSPENSION",
            "COMMUNICATION_RESTRICTION"
        ];

        const show =
            temporaryTypes.includes(penaltyType.value);

        durationFields.classList.toggle(
            "visible",
            show
        );

        durationAmount.required = show;
        durationUnit.required = show;

        if (!show) {
            durationAmount.value = "";
            durationUnit.value = "";
        }
    }

    // Custom reason box: shown only for "Other or platform-specific reason"
    function updateCustomReason() {
        const show =
            reason.value === "OTHER_PLATFORM_SPECIFIC";

        customReasonGroup.classList.toggle(
            "visible",
            show
        );

        customReason.required = show;

        if (!show) {
            customReason.value = "";
        }
    }

    // Evidence textarea: shown only when "Yes" is picked
    function updateEvidence() {
        const selected =
            document.querySelector(
                "input[name='platformProvidedEvidence']:checked"
            );

        const show =
            selected !== null
            && selected.value === "true";

        evidenceGroup.classList.toggle(
            "visible",
            show
        );

        evidenceText.required = show;

        if (!show) {
            evidenceText.value = "";
        }
    }

    // Re-run each rule whenever its controlling field changes
    penaltyType.addEventListener(
        "change",
        updateDuration
    );

    reason.addEventListener(
        "change",
        updateCustomReason
    );

    evidenceRadios.forEach(radio => {
        radio.addEventListener(
            "change",
            updateEvidence
        );
    });

    // Run once on load: when the form comes back with errors, the right
    // fields must already be visible
    updateDuration();
    updateCustomReason();
    updateEvidence();
});