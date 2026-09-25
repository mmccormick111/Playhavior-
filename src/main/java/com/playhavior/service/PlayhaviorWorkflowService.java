package com.playhavior.service;

import com.playhavior.entity.BanReport;
import com.playhavior.entity.Case;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.Platform;
import com.playhavior.entity.PlatformPolicy;
import com.playhavior.entity.Player;
import com.playhavior.entity.SummaryReport;
import com.playhavior.model.MappingResult;
import com.playhavior.model.PathwayCode;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.PersonalizationLevel;
import com.playhavior.repository.BanReportRepository;
import com.playhavior.repository.CaseRepository;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.repository.PlatformRepository;
import com.playhavior.repository.PlayerProfileRepository;
import com.playhavior.repository.SummaryReportRepository;
import com.playhavior.web.form.ViolationInputForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PlayhaviorWorkflowService {

    private final PlayerProfileRepository playerProfileRepository;
    private final CaseRepository caseRepository;
    private final BanReportRepository banReportRepository;
    private final LearningPathwayRepository learningPathwayRepository;
    private final SummaryReportRepository summaryReportRepository;
    private final PlatformRepository platformRepository;
    private final PlatformPolicyService platformPolicyService;
    private final CategoryMappingService categoryMappingService;
    private final PenaltyEligibilityService penaltyEligibilityService;

    public PlayhaviorWorkflowService(
            PlayerProfileRepository playerProfileRepository,
            CaseRepository caseRepository,
            BanReportRepository banReportRepository,
            LearningPathwayRepository learningPathwayRepository,
            SummaryReportRepository summaryReportRepository,
            PlatformRepository platformRepository,
            PlatformPolicyService platformPolicyService,
            CategoryMappingService categoryMappingService,
            PenaltyEligibilityService penaltyEligibilityService
    ) {
        this.playerProfileRepository = playerProfileRepository;
        this.caseRepository = caseRepository;
        this.banReportRepository = banReportRepository;
        this.learningPathwayRepository = learningPathwayRepository;
        this.summaryReportRepository = summaryReportRepository;
        this.platformRepository = platformRepository;
        this.platformPolicyService = platformPolicyService;
        this.categoryMappingService = categoryMappingService;
        this.penaltyEligibilityService = penaltyEligibilityService;
    }

    @Transactional
    public LearningPathway startWorkflow(
            Player player,
            ViolationInputForm form
    ) {
        Player savedPlayer =
                playerProfileRepository.save(player);

        Platform platform = findPlatform(
                form.getPlatformKey()
        );

        MappingResult mapping =
                categoryMappingService.mapReason(
                        form.getViolationReasonKey()
                );

        PathwayMode pathwayMode =
                penaltyEligibilityService.determineMode(
                        form.getPenaltyType(),
                        form.getPenaltyDurationAmount(),
                        form.getPenaltyDurationUnit()
                );

        PersonalizationLevel personalizationLevel =
                determinePersonalization(form);

        PlatformPolicy activePolicy =
                platformPolicyService.findActivePolicy(
                        platform.getPlatformKey()
                );

        BanReport banReport = buildBanReport(
                form,
                platform,
                mapping
        );

        BanReport savedBanReport =
                banReportRepository.save(banReport);

        Case playerCase = buildCase(
                savedPlayer,
                savedBanReport,
                form
        );

        Case savedCase =
                caseRepository.save(playerCase);

        LearningPathway pathway = buildLearningPathway(
                savedCase,
                mapping,
                pathwayMode,
                personalizationLevel,
                activePolicy
        );

        return learningPathwayRepository.save(pathway);
    }

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

        boolean platformProvidedEvidence =
                Boolean.TRUE.equals(
                        form.getPlatformProvidedEvidence()
                );

        banReport.setPlatformProvidedEvidence(
                platformProvidedEvidence
        );

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

        banReport.setSubmittedAt(
                LocalDateTime.now()
        );

        return banReport;
    }

    private Case buildCase(
            Player player,
            BanReport banReport,
            ViolationInputForm form
    ) {
        Case playerCase = new Case();

        playerCase.setDescription(
                "Accountability case for "
                        + determineStatedReason(form)
        );

        playerCase.setStatus("Open");

        playerCase.setPlayer(player);
        playerCase.setBanReport(banReport);

        return playerCase;
    }

    private LearningPathway buildLearningPathway(
            Case playerCase,
            MappingResult mapping,
            PathwayMode pathwayMode,
            PersonalizationLevel personalizationLevel,
            PlatformPolicy activePolicy
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

        pathway.setPersonalizationLevel(
                personalizationLevel
        );

        pathway.setPathway_title(
                titleFor(mapping.pathway())
        );

        pathway.setTotal_modules(
                moduleCountFor(pathwayMode)
        );

        pathway.setGeneratedAt(
                LocalDateTime.now()
        );

        if (activePolicy != null) {
            pathway.setPlatformPolicy(activePolicy);
        }

        pathway.setPlayerCase(playerCase);

        return pathway;
    }

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

    private String cleanOptionalText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

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

    private int moduleCountFor(
            PathwayMode pathwayMode
    ) {
        return switch (pathwayMode) {
            case REINSTATEMENT_SUPPORT -> 3;
            case EDUCATIONAL_ONLY -> 2;
        };
    }

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
                        pathway.getPathway_title()
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