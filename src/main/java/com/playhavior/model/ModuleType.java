package com.playhavior.model;

/**
 * What kind of interactive module a PathwayModule is. Each type has its own
 * page template and grading rules (ModuleProgressService).
 *
 * Framework phase -> module type:
 *   Phase 1 Contextual Disorientation -> VALIDATION_HUB
 *   Phase 2 Track A (social)          -> PERSPECTIVE_SHIFT
 *   Phase 2 Track B (systemic)        -> RISK_MATRIX
 *   Phase 3 Branching assessment      -> ACCOUNTABILITY_SANDBOX
 *   Phase 4 Contract + data export    -> PROBATIONARY_CONTRACT
 */
public enum ModuleType {

    VALIDATION_HUB("module-validation-hub"),
    PERSPECTIVE_SHIFT("module-perspective-shift"),
    RISK_MATRIX("module-risk-matrix"),
    ACCOUNTABILITY_SANDBOX("module-sandbox"),
    PROBATIONARY_CONTRACT("module-contract");

    // The Thymeleaf template (in templates/) that renders this module type
    private final String templateName;

    ModuleType(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
