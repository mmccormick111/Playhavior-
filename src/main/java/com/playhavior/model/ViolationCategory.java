package com.playhavior.model;

/**
 * The 14 broad violation groups (the internal vocabulary).
 * CategoryMappingService maps each dropdown reason to one of these; the policy CSV
 * and PolicyRule use them to match rules. Names must match the CSV column exactly.
 */
public enum ViolationCategory {
    HARASSMENT_BULLYING,
    HATE_DISCRIMINATION,
    THREATS_VIOLENT_INTIMIDATION,
    SELF_HARM_SUICIDE_ENCOURAGEMENT,
    SEXUAL_HARASSMENT_SEXUAL_CONTENT,
    PRIVACY_DOXXING,
    IMPERSONATION_DECEPTION,
    SPAM_COMMUNICATION_ABUSE,
    SCAMS_FRAUD_PHISHING,
    CHEATING_EXPLOITS_UNFAIR_PLAY,
    ILLEGAL_DANGEROUS_ACTIVITY,
    ACCOUNT_PLATFORM_ABUSE,
    INAPPROPRIATE_EXPLICIT_CONTENT,
    OTHER_PLATFORM_SPECIFIC
}
