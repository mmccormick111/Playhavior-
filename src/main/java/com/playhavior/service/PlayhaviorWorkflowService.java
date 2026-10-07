package com.playhavior.service;

import com.playhavior.entity.BanReport;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.Platform;
import com.playhavior.entity.PlatformPolicy;
import com.playhavior.entity.Player;
import com.playhavior.entity.PlayerCase;
import com.playhavior.entity.SummaryReport;
import com.playhavior.model.MappingResult;
import com.playhavior.model.PathwayCode;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.PersonalizationLevel;
import com.playhavior.repository.BanReportRepository;
import com.playhavior.repository.PlayerCaseRepository;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.repository.PlatformRepository;
import com.playhavior.repository.SummaryReportRepository;
import com.playhavior.web.form.ViolationInputForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * The "director" of the notice-to-pathway process (service layer).
 *
 * FLOW: steps 4 and 5. Decides everything first, then saves everything.
 * CALLED BY: ViolationIntakeController.submitForm()
 * CALLS: CategoryMappingService, PenaltyEligibilityService, PlatformPolicyService,
 *        ModulePlanService, and the BanReport / PlayerCase / LearningPathway repositories.
 * WHY a separate service: the controller stays thin, and these rules can be
 *     tested and reused (e.g. by the dashboard) without the web layer.
 */
@Service
public class PlayhaviorWorkflowService {

    // ===== DEPENDENCIES (constructor injection) =====
    private final PlayerCaseRepository caseRepository;
    private final BanReportRepository banReportRepository;
    private final LearningPathwayRepository learningPathwayRepository;
    private final SummaryReportRepository summaryReportRepository;
    private final PlatformRepository platformRepository;
    private final PlatformPolicyService platformPolicyService;
    private final CategoryMappingService categoryMappingService;
    private final PenaltyEligibilityService penaltyEligibilityService;
    private final ModulePlanService modulePlanService;

    public PlayhaviorWorkflowService(
            PlayerCaseRepository caseRepository,
            BanReportRepository banReportRepository,
            LearningPathwayRepository learningPathwayRepository,
            SummaryReportRepository summaryReportRepository,
            PlatformRepository platformRepository,
            PlatformPolicyService platformPolicyService,
            CategoryMappingService categoryMappingService,
            PenaltyEligibilityService penaltyEligibilityService,
            ModulePlanService modulePlanService
    ) {
        this.caseRepository = caseRepository;
        this.banReportRepository = banReportRepository;
        this.learningPathwayRepository = learningPathwayRepository;
        this.summaryReportRepository = summaryReportRepository;
        this.platformRepository = platformRepository;
        this.platformPolicyService = platformPolicyService;
        this.categoryMappingService = categoryMappingService;
        this.penaltyEligibilityService = penaltyEligibilityService;
        this.modulePlanService = modulePlanService;
    }

    // ===== MAIN WORKFLOW (public) =====

    /**
     * Turns a valid form into saved records and returns the new pathway.
     * WHY @Transactional: every save below succeeds together or is rolled back
     *     together, so a failure never leaves a report with no pathway.
     * Q: Why this save order? Each record needs the id of the one before it.
     * RETURNS: the saved pathway, so the controller can redirect to its id.
     */
    @Transactional
    public LearningPathway startWorkflow(
            Player player,
            ViolationInputForm form
    ) {
        // ----- PHASE 1: DECIDE (FLOW step 4). Nothing is saved yet, so bad input fails early -----

        // 1a. Look up the platform from its key (throws if the key was tampered with)
        Platform platform = findPlatform(
                form.getPlatformKey()
        );

        // 1b. Reason key -> category -> pathway code + track (HashMap lookups + a switch)
        MappingResult mapping =
                categoryMappingService.mapReason(
                        form.getViolationReasonKey()
                );

        // 1c. Ban type + length -> REINSTATEMENT_SUPPORT (4 modules) or EDUCATIONAL_ONLY (3)
        PathwayMode pathwayMode =
                penaltyEligibilityService.determineMode(
                        form.getPenaltyType(),
                        form.getPenaltyDurationAmount(),
                        form.getPenaltyDurationUnit()
                );

        // 1d. PERSONALIZED if the player pasted evidence; kept for tailoring content later
        PersonalizationLevel personalizationLevel =
                determinePersonalization(form);

        // 1e. The platform's current code of conduct, cited on the pathway page
        PlatformPolicy activePolicy =
                platformPolicyService.findActivePolicy(
                        platform.getPlatformKey()
                );

        // ----- PHASE 2: SAVE (FLOW step 5), in dependency order -----

        // 2a. The notice details (the ban_reports row to check in H2)
        BanReport banReport = buildBanReport(
                form,
                platform,
                mapping
        );

        BanReport savedBanReport =
                banReportRepository.save(banReport);

        // 2b. The case links the player to the report (needs the report's id)
        PlayerCase playerCase = buildCase(
                player,
                savedBanReport,
                form
        );

        PlayerCase savedCase =
                caseRepository.save(playerCase);

        // 2c. The pathway + its modules (needs the case's id).
        //     WHY no module save: cascade = ALL on LearningPathway.modules saves them too.
        LearningPathway pathway = buildLearningPathway(
                savedCase,
                mapping,
                pathwayMode,
                personalizationLevel,
                activePolicy,
                platform
        );

        return learningPathwayRepository.save(pathway);
    }

    // ===== BUILD HELPERS (private: only this class uses them) =====

    // Platform by key, or IllegalArgumentException.
    // WHY: fail loudly on a dropdown value that is not a supported platform.
    private Platform findPlatform(String platformKey) {
        return platformRepository
                .findByPlatformKey(platformKey)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unsupported platform selection: "
                                        + platformKey
                        )
                );
    }

    // Copies the form into a new BanReport (not saved yet).
    // WHY here and not in the entity: keeps form logic out of the data model.
    private BanReport buildBanReport(
            ViolationInputForm form,
            Platform platform,
            MappingResult mapping
    ) {
        BanReport banReport = new BanReport();

        banReport.setPlatform(platform);

        banReport.setStated_reason(
                determineStatedReason(form)
        );

        banReport.setGameTitle(
                cleanOptionalText(
                        form.getGameTitle()
                )
        );

        banReport.setViolationReasonKey(
                form.getViolationReasonKey()
        );

        banReport.setViolationCategory(
                mapping.category()
        );

        banReport.setPenaltyType(
                form.getPenaltyType()
        );

        banReport.setPenaltyDurationAmount(
                form.getPenaltyDurationAmount()
        );

        banReport.setPenaltyDurationUnit(
                form.getPenaltyDurationUnit()
        );

        // WHY Boolean.TRUE.equals(...): safe even if the value is null
        boolean platformProvidedEvidence =
                Boolean.TRUE.equals(
                        form.getPlatformProvidedEvidence()
                );

        banReport.setPlatformProvidedEvidence(
                platformProvidedEvidence
        );

        // Evidence text is only kept when the player answered "Yes"
        if (platformProvidedEvidence) {
            banReport.setEvidenceText(
                    cleanOptionalText(
                            form.getEvidenceText()
                    )
            );
        } else {
            banReport.setEvidenceText(null);
        }

        banReport.setBanIssueDate(
                form.getBanIssueDate()
        );

        banReport.setPlatformCaseNumber(
                cleanOptionalText(
                        form.getPlatformCaseNumber()
                )
        );

        // Timestamp of the submission (the notice itself only has a date)
        banReport.setSubmittedAt(
                LocalDateTime.now()
        );

        return banReport;
    }

    // New case with status "Open", linking the player to the saved report
    private PlayerCase buildCase(
            Player player,
            BanReport banReport,
            ViolationInputForm form
    ) {
        PlayerCase playerCase = new PlayerCase();

        playerCase.setDescription(
                "Accountability case for "
                        + determineStatedReason(form)
        );

        playerCase.setStatus("Open");

        playerCase.setPlayer(player);
        playerCase.setBanReport(banReport);

        return playerCase;
    }

    // Gathers every decision into one LearningPathway (not saved yet).
    // CALLS: ModulePlanService.buildPlan(); addModule() links each module to this pathway.
    private LearningPathway buildLearningPathway(
            PlayerCase playerCase,
            MappingResult mapping,
            PathwayMode pathwayMode,
            PersonalizationLevel personalizationLevel,
            PlatformPolicy activePolicy,
            Platform platform
    ) {
        LearningPathway pathway =
                new LearningPathway();

        pathway.setViolationCategory(
                mapping.category()
        );

        pathway.setPathwayCode(
                mapping.pathway()
        );

        pathway.setPathwayMode(
                pathwayMode
        );

        // Social & Behavioral or System Integrity (framework Phase 2 routing)
        pathway.setTrack(
                mapping.track()
        );

        pathway.setPersonalizationLevel(
                personalizationLevel
        );

        String pathwayTitle =
                titleFor(mapping.pathway());

        pathway.setPathwayTitle(pathwayTitle);

        // The four-phase module list; pathway::addModule is a method reference,
        // meaning "call pathway.addModule(module) for each module"
        modulePlanService
                .buildPlan(
                        pathwayMode,
                        mapping.track(),
                        platform.getDisplayName()
                )
                .forEach(pathway::addModule);

        pathway.setGeneratedAt(
                LocalDateTime.now()
        );

        // NOTE: findActivePolicy() throws when a platform has no policy,
        // so this check is always true today.
        if (activePolicy != null) {
            pathway.setPlatformPolicy(activePolicy);
        }

        pathway.setPlayerCase(playerCase);

        return pathway;
    }

    // ===== SMALL DECISION / TEXT HELPERS (private) =====

    // PERSONALIZED only when evidence was given AND the text is not blank
    private PersonalizationLevel determinePersonalization(
            ViolationInputForm form
    ) {
        boolean hasUsableEvidence =
                Boolean.TRUE.equals(
                        form.getPlatformProvidedEvidence()
                )
                        && form.getEvidenceText() != null
                        && !form.getEvidenceText().isBlank();

        return hasUsableEvidence
                ? PersonalizationLevel.PERSONALIZED
                : PersonalizationLevel.GENERIC;
    }

    // For "Other", the player's own words; otherwise a readable version of the key.
    // WHY: stated_reason always holds something a person can read.
    private String determineStatedReason(
            ViolationInputForm form
    ) {
        if ("OTHER_PLATFORM_SPECIFIC".equals(
                form.getViolationReasonKey()
        )) {
            String customReason =
                    cleanOptionalText(
                            form.getCustomStatedReason()
                    );

            if (customReason != null) {
                return customReason;
            }
        }

        return makeReasonReadable(
                form.getViolationReasonKey()
        );
    }

    // PERSONAL_INSULTS -> "Personal insults"
    private String makeReasonReadable(
            String reasonKey
    ) {
        if (reasonKey == null || reasonKey.isBlank()) {
            return "Unspecified conduct violation";
        }

        String readableReason =
                reasonKey
                        .replace('_', ' ')
                        .toLowerCase();

        return Character.toUpperCase(
                readableReason.charAt(0)
        ) + readableReason.substring(1);
    }

    // Blank -> null, otherwise trimmed.
    // WHY: "nothing entered" is always stored as NULL, never as "" or "   ".
    private String cleanOptionalText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // Human title for each pathway code.
    // WHY a switch expression: it must cover every PathwayCode, so adding a new
    // code without a title will not compile.
    private String titleFor(
            PathwayCode pathwayCode
    ) {
        return switch (pathwayCode) {
            case RESPECTFUL_COMMUNICATION ->
                    "Respectful Communication";

            case INCLUSION_AND_ANTI_DISCRIMINATION ->
                    "Inclusion and Anti-Discrimination";

            case THREAT_DEESCALATION ->
                    "Threat Awareness and De-escalation";

            case SAFE_RESPONSE_TO_SELF_HARM_LANGUAGE ->
                    "Safe Responses to Self-Harm Language";

            case CONSENT_AND_SEXUAL_BOUNDARIES ->
                    "Consent and Sexual Boundaries";

            case PRIVACY_AND_PERSONAL_INFORMATION ->
                    "Privacy and Personal Information";

            case HONEST_IDENTITY_AND_COMMUNICATION ->
                    "Honest Identity and Communication";

            case RESPONSIBLE_COMMUNICATION_USE ->
                    "Responsible Communication Use";

            case DIGITAL_SCAM_AND_ACCOUNT_SAFETY ->
                    "Digital Scam and Account Safety";

            case FAIR_PLAY_AND_GAME_INTEGRITY ->
                    "Fair Play and Game Integrity";

            case SAFETY_AND_LEGAL_BOUNDARIES ->
                    "Safety and Legal Boundaries";

            case ACCOUNT_AND_PLATFORM_RESPONSIBILITY ->
                    "Account and Platform Responsibility";

            case RESPONSIBLE_CONTENT_SHARING ->
                    "Responsible Content Sharing";

            case COMMUNITY_STANDARDS_FOUNDATION ->
                    "Community Standards Foundation";
        };
    }

    // ===== COMPLETION (not used yet) =====

    /**
     * Creates a SummaryReport with a narrative and a random 6-digit verification code.
     * TODO: not called anywhere yet. Groundwork for the Completion page;
     *       link SummaryReport to the pathway (and its reference code) first.
     */
    @Transactional
    public SummaryReport completePathway(
            Long pathwayId
    ) {
        LearningPathway pathway =
                learningPathwayRepository
                        .findById(pathwayId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Learning pathway not found."
                                )
                        );

        SummaryReport summaryReport =
                new SummaryReport();

        summaryReport.setNarrative_text(
                summaryFor(
                        pathway.getPathwayTitle()
                )
        );

        summaryReport.setVerification_code(
                (int) (
                        100000
                                + Math.random()
                                * 900000
                )
        );

        return summaryReportRepository.save(
                summaryReport
        );
    }

    private String summaryFor(
            String pathwayTitle
    ) {
        return "The player completed the "
                + pathwayTitle
                + ". The player reviewed accountability, "
                + "the impact of their conduct, and the "
                + "platform standards relevant to the case.";
    }
}