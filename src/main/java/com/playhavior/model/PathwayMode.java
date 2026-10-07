package com.playhavior.model;

/**
 * Appeal support (4 modules, ending in the Probationary Contract + appeal packet)
 * or education only (3 modules, no appeal packet).
 * Decided by PenaltyEligibilityService from the ban type and length.
 */
public enum PathwayMode {
    REINSTATEMENT_SUPPORT,
    EDUCATIONAL_ONLY
}
