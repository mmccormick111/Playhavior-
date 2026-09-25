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

    updateDuration();
    updateCustomReason();
    updateEvidence();
});