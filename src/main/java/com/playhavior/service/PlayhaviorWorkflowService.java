package com.playhavior.service;

import com.playhavior.entity.BanReport;
import com.playhavior.entity.Case;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.Player;
import com.playhavior.entity.SummaryReport;
import com.playhavior.repository.BanReportRepository;
import com.playhavior.repository.CaseRepository;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.repository.PlayerProfileRepository;
import com.playhavior.repository.SummaryReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayhaviorWorkflowService {

    private final PlayerProfileRepository playerProfileRepository;
    private final CaseRepository caseRepository;
    private final BanReportRepository banReportRepository;
    private final LearningPathwayRepository learningPathwayRepository;
    private final SummaryReportRepository summaryReportRepository;

    public PlayhaviorWorkflowService(
            PlayerProfileRepository playerProfileRepository,
            CaseRepository caseRepository,
            BanReportRepository banReportRepository,
            LearningPathwayRepository learningPathwayRepository,
            SummaryReportRepository summaryReportRepository) {

        this.playerProfileRepository = playerProfileRepository;
        this.caseRepository = caseRepository;
        this.banReportRepository = banReportRepository;
        this.learningPathwayRepository = learningPathwayRepository;
        this.summaryReportRepository = summaryReportRepository;
    }

    @Transactional
    public LearningPathway startWorkflow(
            String displayName,
            String email,
            String password,
            String statedReason,
            String platform) {

        Player player = new Player();
        player.setDisplay_name(displayName);
        player.setEmail(email);
        player.setPassword(password);
        playerProfileRepository.save(player);

        Case playerCase = new Case();
        playerCase.setDescription(statedReason);
        playerCase.setStatus("Open");
        caseRepository.save(playerCase);

        BanReport banReport = new BanReport();
        banReport.setStated_reason(statedReason);
        banReport.setPlatform(platform);
        banReportRepository.save(banReport);

        LearningPathway pathway = new LearningPathway();
        pathway.setPathway_title(pathwayTitleFor(statedReason));
        pathway.setTotal_modules(moduleCountFor(statedReason));
        return learningPathwayRepository.save(pathway);
    }

    @Transactional
    public SummaryReport completePathway(Long pathwayId) {
        LearningPathway pathway = learningPathwayRepository.findById(pathwayId)
                .orElseThrow(() -> new IllegalArgumentException("Learning pathway not found"));

        SummaryReport summaryReport = new SummaryReport();
        summaryReport.setNarrative_text(summaryFor(pathway.getPathway_title()));
        summaryReport.setVerification_code((int) (100000 + Math.random() * 900000));

        return summaryReportRepository.save(summaryReport);
    }

    private String pathwayTitleFor(String statedReason) {
        return switch (statedReason) {
            case "Harassment or abusive chat" ->
                    "Respectful Communication Module";

            case "Hate speech or discriminatory language" ->
                    "Inclusive Gaming and Community Respect Module";

            case "Griefing or intentional disruption" ->
                    "Teamplay, Fair Competition, and Sportsmanship Module";

            case "Cheating or exploiting game systems" ->
                    "Competitive Integrity and Fair Play Module";

            default ->
                    "Responsible Gaming Conduct Module";
        };
    }

    private int moduleCountFor(String statedReason) {
        return switch (statedReason) {
            case "Hate speech or discriminatory language" -> 3;
            case "Cheating or exploiting game systems" -> 3;
            case "Harassment or abusive chat" -> 2;
            case "Griefing or intentional disruption" -> 2;
            default -> 1;
        };
    }

    private String summaryFor(String pathwayTitle) {
        return "The player completed the " + pathwayTitle
                + ". The player reviewed accountability, respectful conduct, "
                + "and the expected standards for re-entry into the gaming community.";
    }
}
