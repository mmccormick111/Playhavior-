package com.playhavior.model;

/**
 * The two specialised learning tracks a pathway is routed into (framework Phase 2).
 *
 *   SOCIAL_BEHAVIORAL -> Restorative Justice: empathy and perspective shift
 *                        (hate, harassment, bullying, spam, ...)
 *   SYSTEM_INTEGRITY  -> Cognitive pre-training and risk assessment
 *                        (cheating, exploits, scams/phishing, account abuse)
 * DECIDED BY: CategoryMappingService.trackFor(category)
 */
public enum ReintegrationTrack {

    SOCIAL_BEHAVIORAL(
            "Social & Behavioral Track",
            "Empathy & Perspective Shift"
    ),

    SYSTEM_INTEGRITY(
            "System Integrity Track",
            "System Integrity & Security Risk"
    );

    private final String label;
    private final String focus;

    ReintegrationTrack(String label, String focus) {
        this.label = label;
        this.focus = focus;
    }

    public String getLabel() {
        return label;
    }

    public String getFocus() {
        return focus;
    }
}
