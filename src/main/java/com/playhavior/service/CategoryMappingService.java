package com.playhavior.service;

import com.playhavior.model.MappingResult;
import com.playhavior.model.PathwayCode;
import com.playhavior.model.ViolationCategory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CategoryMappingService {

    /*
     * First HashMap:
     * Maps the specific dropdown selection to a top-level category.
     */
    private final Map<String, ViolationCategory> reasonToCategory =
            new HashMap<>();

    /*
     * Second HashMap:
     * Maps the top-level category to its learning pathway.
     */
    private final Map<ViolationCategory, PathwayCode> categoryToPathway =
            new HashMap<>();

    public CategoryMappingService() {
        initializeReasonMappings();
        initializePathwayMappings();
    }

    private void initializeReasonMappings() {

        // Harassment / Bullying
        addReasons(
                ViolationCategory.HARASSMENT_BULLYING,
                "PERSONAL_INSULTS",
                "TARGETED_HARASSMENT",
                "BULLYING_HUMILIATION",
                "NONVIOLENT_INTIMIDATION",
                "PERSISTENT_UNWANTED_CONTACT",
                "GRIEFING"
        );

        // Hate / Discrimination
        addReasons(
                ViolationCategory.HATE_DISCRIMINATION,
                "RACIAL_ETHNIC_DISCRIMINATION",
                "SEXUAL_ORIENTATION_DISCRIMINATION",
                "GENDER_IDENTITY_DISCRIMINATION",
                "RELIGIOUS_DISCRIMINATION",
                "DISABILITY_DISCRIMINATION",
                "AGE_DISCRIMINATION",
                "OTHER_PROTECTED_CHARACTERISTIC"
        );

        // Threats / Violent Intimidation
        addReasons(
                ViolationCategory.THREATS_VIOLENT_INTIMIDATION,
                "DIRECT_THREAT",
                "IMPLIED_THREAT",
                "GROUP_THREAT",
                "REAL_WORLD_RETALIATION",
                "VIOLENT_INTIMIDATION"
        );

        // Self-Harm / Suicide Encouragement
        addReasons(
                ViolationCategory.SELF_HARM_SUICIDE_ENCOURAGEMENT,
                "SUICIDE_ENCOURAGEMENT",
                "SELF_HARM_ENCOURAGEMENT",
                "HARM_WISHING_KYS",
                "PROMOTION_OF_HARMFUL_BEHAVIOR"
        );

        // Sexual Harassment / Sexual Content
        addReasons(
                ViolationCategory.SEXUAL_HARASSMENT_SEXUAL_CONTENT,
                "UNWANTED_SEXUAL_PROPOSITION",
                "SEXUAL_HARASSMENT",
                "SEXUALIZED_INSULT",
                "EXPLICIT_SEXUAL_MESSAGE",
                "NONCONSENSUAL_SEXUAL_CONTENT"
        );

        // Privacy / Doxxing
        addReasons(
                ViolationCategory.PRIVACY_DOXXING,
                "REAL_NAME_ADDRESS_EXPOSURE",
                "PHONE_EMAIL_EXPOSURE",
                "SOCIAL_ACCOUNT_EXPOSURE",
                "LOCATION_EXPOSURE",
                "THREAT_TO_DOX",
                "SHARING_PRIVATE_MEDIA"
        );

        // Impersonation / Deception
        addReasons(
                ViolationCategory.IMPERSONATION_DECEPTION,
                "PLAYER_IMPERSONATION",
                "CREATOR_IMPERSONATION",
                "EMPLOYEE_MODERATOR_IMPERSONATION",
                "DECEPTIVE_IDENTITY_CLAIMS",
                "FALSE_ATTRIBUTION"
        );

        // Spam / Communication Abuse
        addReasons(
                ViolationCategory.SPAM_COMMUNICATION_ABUSE,
                "REPEATED_MESSAGES",
                "MESSAGE_FLOODING",
                "REPEATED_INVITES",
                "OFF_TOPIC_FLOODING",
                "VOICE_CHANNEL_DISRUPTION"
        );

        // Scams / Fraud / Phishing
        addReasons(
                ViolationCategory.SCAMS_FRAUD_PHISHING,
                "CREDENTIAL_PHISHING",
                "ACCOUNT_THEFT_ATTEMPT",
                "FAKE_REWARD_CURRENCY_OFFER",
                "FRAUDULENT_TRADE",
                "ACCOUNT_SALE_SCAM",
                "FINANCIAL_DECEPTION"
        );

        // Cheating / Exploits / Unfair Play
        addReasons(
                ViolationCategory.CHEATING_EXPLOITS_UNFAIR_PLAY,
                "CHEAT_PROMOTION",
                "HACK_EXPLOIT_PROMOTION",
                "BOTTING_SCRIPTING",
                "RANK_MANIPULATION",
                "MATCH_FIXING_WIN_TRADING",
                "EXPLOIT_ABUSE"
        );

        // Illegal / Dangerous Activity
        addReasons(
                ViolationCategory.ILLEGAL_DANGEROUS_ACTIVITY,
                "ILLEGAL_ACTIVITY_PROMOTION",
                "VIOLENCE_INCITEMENT",
                "DANGEROUS_ACTIVITY_ENCOURAGEMENT",
                "TERRORIST_EXTREMIST_ACTIVITY",
                "HUMAN_TRAFFICKING_EXPLOITATION"
        );

        // Account / Platform Abuse
        addReasons(
                ViolationCategory.ACCOUNT_PLATFORM_ABUSE,
                "ACCOUNT_SHARING",
                "ACCOUNT_BUYING_SELLING",
                "BAN_EVASION",
                "UNAUTHORIZED_ACCOUNT_ACCESS",
                "PLATFORM_ABUSE"
        );

        // Inappropriate / Explicit Content
        addReasons(
                ViolationCategory.INAPPROPRIATE_EXPLICIT_CONTENT,
                "PORNOGRAPHIC_EXPLICIT_CONTENT",
                "GRAPHIC_REAL_WORLD_VIOLENCE",
                "EXPLICIT_PROFILE_MEDIA",
                "PROHIBITED_IMAGERY"
        );

        // Other / Platform-Specific
        addReasons(
                ViolationCategory.OTHER_PLATFORM_SPECIFIC,
                "SPOILERS_LEAKS",
                "INTELLECTUAL_PROPERTY_MISUSE",
                "PLATFORM_SPECIFIC_CONDUCT",
                "OTHER_REPORTED_BEHAVIOR",
                "UNSPECIFIED_CONDUCT",
                "OTHER_PLATFORM_SPECIFIC"
        );
    }

    private void initializePathwayMappings() {

        categoryToPathway.put(
                ViolationCategory.HARASSMENT_BULLYING,
                PathwayCode.RESPECTFUL_COMMUNICATION
        );

        categoryToPathway.put(
                ViolationCategory.HATE_DISCRIMINATION,
                PathwayCode.INCLUSION_AND_ANTI_DISCRIMINATION
        );

        categoryToPathway.put(
                ViolationCategory.THREATS_VIOLENT_INTIMIDATION,
                PathwayCode.THREAT_DEESCALATION
        );

        categoryToPathway.put(
                ViolationCategory.SELF_HARM_SUICIDE_ENCOURAGEMENT,
                PathwayCode.SAFE_RESPONSE_TO_SELF_HARM_LANGUAGE
        );

        categoryToPathway.put(
                ViolationCategory.SEXUAL_HARASSMENT_SEXUAL_CONTENT,
                PathwayCode.CONSENT_AND_SEXUAL_BOUNDARIES
        );

        categoryToPathway.put(
                ViolationCategory.PRIVACY_DOXXING,
                PathwayCode.PRIVACY_AND_PERSONAL_INFORMATION
        );

        categoryToPathway.put(
                ViolationCategory.IMPERSONATION_DECEPTION,
                PathwayCode.HONEST_IDENTITY_AND_COMMUNICATION
        );

        categoryToPathway.put(
                ViolationCategory.SPAM_COMMUNICATION_ABUSE,
                PathwayCode.RESPONSIBLE_COMMUNICATION_USE
        );

        categoryToPathway.put(
                ViolationCategory.SCAMS_FRAUD_PHISHING,
                PathwayCode.DIGITAL_SCAM_AND_ACCOUNT_SAFETY
        );

        categoryToPathway.put(
                ViolationCategory.CHEATING_EXPLOITS_UNFAIR_PLAY,
                PathwayCode.FAIR_PLAY_AND_GAME_INTEGRITY
        );

        categoryToPathway.put(
                ViolationCategory.ILLEGAL_DANGEROUS_ACTIVITY,
                PathwayCode.SAFETY_AND_LEGAL_BOUNDARIES
        );

        categoryToPathway.put(
                ViolationCategory.ACCOUNT_PLATFORM_ABUSE,
                PathwayCode.ACCOUNT_AND_PLATFORM_RESPONSIBILITY
        );

        categoryToPathway.put(
                ViolationCategory.INAPPROPRIATE_EXPLICIT_CONTENT,
                PathwayCode.RESPONSIBLE_CONTENT_SHARING
        );

        categoryToPathway.put(
                ViolationCategory.OTHER_PLATFORM_SPECIFIC,
                PathwayCode.COMMUNITY_STANDARDS_FOUNDATION
        );
    }

    /*
     * Adds several specific violation-reason keys
     * to the same top-level category.
     */
    private void addReasons(
            ViolationCategory category,
            String... reasonKeys
    ) {
        for (String reasonKey : reasonKeys) {
            reasonToCategory.put(reasonKey, category);
        }
    }

    /*
     * Called by PlayhaviorWorkflowService after the user
     * submits the violation questionnaire.
     */
    public MappingResult mapReason(String reasonKey) {

        if (reasonKey == null || reasonKey.isBlank()) {
            throw new IllegalArgumentException(
                    "A violation reason must be selected."
            );
        }

        ViolationCategory category =
                reasonToCategory.get(reasonKey);

        if (category == null) {
            throw new IllegalArgumentException(
                    "Unknown violation reason: " + reasonKey
            );
        }

        PathwayCode pathway =
                categoryToPathway.get(category);

        if (pathway == null) {
            throw new IllegalStateException(
                    "No pathway has been configured for category: "
                            + category
            );
        }

        return new MappingResult(category, pathway);
    }

    /*
     * Used by ViolationInputValidator to ensure that
     * the submitted dropdown value is legitimate.
     */
    public boolean isRecognizedReason(String reasonKey) {
        return reasonKey != null
                && reasonToCategory.containsKey(reasonKey);
    }
}
