package com.playhavior.service;

import com.playhavior.model.MappingResult;
import com.playhavior.model.PathwayCode;
import com.playhavior.model.ReintegrationTrack;
import com.playhavior.model.ViolationCategory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Translates the dropdown reason into a category and a learning pathway (service layer).
 *
 * FLOW: step 4 (mapReason) and step 3 (isRecognizedReason, via the validator).
 * CALLED BY: PlayhaviorWorkflowService.startWorkflow(), ViolationInputValidator.
 *
 * DATA STRUCTURE: two HashMaps, filled once when Spring creates this bean.
 *   reasonToCategory:  75 reason keys -> 14 ViolationCategory values
 *   categoryToPathway: 14 categories  -> 14 PathwayCode values
 * WHY HashMap: constant-time lookup for fixed data read on every submission.
 * WHY two maps: "what kind of violation" is kept separate from "which pathway",
 *     and the category is also what the policy CSV is matched on.
 * KEEP IN SYNC: every <option value> in violation-intake.html must be listed here.
 */
@Service
public class CategoryMappingService {

    // ===== THE TWO LOOKUP TABLES =====

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

    // Fills both maps once, when Spring creates the bean; after that they are only read
    public CategoryMappingService() {
        initializeReasonMappings();
        initializePathwayMappings();
    }

    // ===== MAP 1: REASON -> CATEGORY =====
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

    // ===== MAP 2: CATEGORY -> PATHWAY =====
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

    // ===== HELPERS + PUBLIC LOOKUPS =====

    /*
     * Adds several specific violation-reason keys
     * to the same top-level category.
     * WHY String... (varargs): one call per category, any number of reasons.
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
     * Returns BOTH values together as a MappingResult record.
     * WHY throw on unknown keys: a silent null would crash later,
     *     somewhere much harder to trace.
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

        return new MappingResult(category, pathway, trackFor(category));
    }

    /*
     * Used by ViolationInputValidator to ensure that
     * the submitted dropdown value is legitimate.
     */
    public boolean isRecognizedReason(String reasonKey) {
        return reasonKey != null
                && reasonToCategory.containsKey(reasonKey);
    }

    // ===== TRACK ROUTING (framework Phase 2) =====

    /*
     * Routes each category into one of the two specialised tracks.
     * SOCIAL_BEHAVIORAL: harm done to other PEOPLE (needs perspective transformation).
     * SYSTEM_INTEGRITY:  harm done to the GAME / ACCOUNTS (needs an integrity audit).
     * WHY a switch expression: every ViolationCategory must be routed, so adding a
     *     new category without a track will not compile.
     * NOTE: impersonation, illegal/dangerous activity and "other" are routed to the
     *     social track because they target or endanger other players.
     */
    public ReintegrationTrack trackFor(ViolationCategory category) {
        return switch (category) {
            case SCAMS_FRAUD_PHISHING,
                 CHEATING_EXPLOITS_UNFAIR_PLAY,
                 ACCOUNT_PLATFORM_ABUSE -> ReintegrationTrack.SYSTEM_INTEGRITY;

            case HARASSMENT_BULLYING,
                 HATE_DISCRIMINATION,
                 THREATS_VIOLENT_INTIMIDATION,
                 SELF_HARM_SUICIDE_ENCOURAGEMENT,
                 SEXUAL_HARASSMENT_SEXUAL_CONTENT,
                 PRIVACY_DOXXING,
                 IMPERSONATION_DECEPTION,
                 SPAM_COMMUNICATION_ABUSE,
                 ILLEGAL_DANGEROUS_ACTIVITY,
                 INAPPROPRIATE_EXPLICIT_CONTENT,
                 OTHER_PLATFORM_SPECIFIC -> ReintegrationTrack.SOCIAL_BEHAVIORAL;
        };
    }
}
