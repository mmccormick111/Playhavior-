package com.playhavior.model;

/**
 * The columns of the Risk Matrix (System Integrity track, framework Phase 2B).
 * NOTE: real outcomes vary by platform; these levels are a learning heuristic.
 */
public enum RiskLevel {

    LEGITIMATE("Legitimate Gameplay"),
    SUSPENSION_RISK("Suspension Risk"),
    PERMANENT_BAN_RISK("Permanent Ban Risk");

    private final String label;

    RiskLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
