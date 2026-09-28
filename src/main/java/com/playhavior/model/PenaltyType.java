package com.playhavior.model;

/**
 * The "Ban type" dropdown. Drives the duration rule (ViolationInputValidator)
 * and the pathway mode (PenaltyEligibilityService).
 * NOTE: option values in violation-intake.html must match these names exactly.
 */
public enum PenaltyType {
    PERMANENT_BAN,
    TEMPORARY_BAN,
    ACCOUNT_SUSPENSION,
    COMMUNICATION_RESTRICTION,
    WARNING,
    OTHER
}
