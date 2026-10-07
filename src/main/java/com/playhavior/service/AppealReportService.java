package com.playhavior.service;

import com.playhavior.content.ModuleContentCatalog;
import com.playhavior.entity.BanReport;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PathwayModule;
import com.playhavior.entity.ReintegrationContract;
import com.playhavior.model.ModuleStatus;
import com.playhavior.model.PathwayMode;
import com.playhavior.web.LabelFormatter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Packages a pathway's results into an AppealReport for the platform's
 * moderation team (framework Phase 4: "Probationary Contract & Data Export").
 *
 * CALLED BY: CompletionController (HTML page + JSON export).
 * METRICS (and what each proves to the moderation team):
 *   First-Pass Accuracy  = first-try correct decisions / all graded decisions
 *                          -> did they know the right choice, or guess until it worked?
 *   Engagement Velocity  = time spent vs the minimum time needed to read the modules
 *                          -> did they digest the content or speed-click through it?
 *   Sentiment Alignment  = ReflectionSentimentAnalyzer on the written reflection
 *                          -> accountable language or defensive evasion?
 * WHY computed on demand (not stored): the numbers always match the latest progress.
 */
@Service
public class AppealReportService {

    private final ModuleContentCatalog catalog;
    private final LabelFormatter labels;

    public AppealReportService(ModuleContentCatalog catalog, LabelFormatter labels) {
        this.catalog = catalog;
        this.labels = labels;
    }

    public AppealReport build(LearningPathway pathway) {
        BanReport banReport = pathway.getPlayerCase().getBanReport();
        ReintegrationContract contract = pathway.getContract();

        List<PathwayModule> completed = pathway.getModules().stream()
                .filter(module -> module.getStatus() == ModuleStatus.COMPLETED)
                .toList();

        // First-Pass Accuracy across every graded decision in completed modules
        int questions = completed.stream().mapToInt(PathwayModule::getQuestionCount).sum();
        int firstPass = completed.stream().mapToInt(PathwayModule::getFirstPassCorrect).sum();
        Integer accuracy = questions == 0 ? null : Math.round(firstPass * 100f / questions);

        // Engagement Velocity across completed modules
        long seconds = completed.stream().mapToLong(PathwayModule::getSecondsSpent).sum();
        long minimum = completed.stream()
                .mapToLong(module -> catalog.minimumEngagedSeconds(module.getModuleType()))
                .sum();

        boolean appealPacket = pathway.getPathwayMode() == PathwayMode.REINSTATEMENT_SUPPORT;

        return new AppealReport(
                pathway.getPathwayId(),
                pathway.getPlayerCase().getPlayer().getDisplay_name(),
                banReport.getPlatform().getDisplayName(),
                banReport.getGameTitle(),
                banReport.getStated_reason(),
                labels.of(pathway.getViolationCategory()),
                pathway.getTrack().getLabel(),
                labels.of(pathway.getPathwayMode()),
                banReport.getPlatformCaseNumber(),
                banReport.getBanIssueDate(),
                pathway.getCompletedModules(),
                pathway.getTotalModules(),
                appealPacket,
                appealPacket && pathway.isFinished(),
                accuracy,
                completed.isEmpty() ? "Not started" : engagementLabel(seconds, minimum),
                seconds,
                minimum,
                contract == null ? null : contract.getSentimentLabel().getLabel(),
                contract == null ? null : contract.getSentimentScore(),
                pathway.getStandingPoints(),
                contract == null
                        ? List.of()
                        : contract.getPledgeKeys().stream().map(catalog::pledgeText).toList(),
                contract == null ? null : contract.getReflectionText(),
                pathway.getModules().stream().map(this::toResult).toList(),
                LocalDateTime.now()
        );
    }

    // One row of the per-module table
    private AppealReport.ModuleResult toResult(PathwayModule module) {
        int minimum = catalog.minimumEngagedSeconds(module.getModuleType());
        boolean done = module.getStatus() == ModuleStatus.COMPLETED;

        return new AppealReport.ModuleResult(
                module.getModuleOrder(),
                module.getTitle(),
                labels.of(module.getStatus()),
                module.getQuestionCount(),
                module.getFirstPassCorrect(),
                module.getTotalAttempts(),
                module.getSecondsSpent(),
                minimum,
                done ? engagementLabel(module.getSecondsSpent(), minimum) : "-",
                module.getStandingPoints()
        );
    }

    /*
     * At or above the minimum reading time -> Engaged; at least half -> Brisk;
     * under half -> Speed-clicked.
     */
    static String engagementLabel(long seconds, long minimum) {
        if (minimum <= 0 || seconds >= minimum) {
            return "Engaged";
        }
        return seconds * 2 >= minimum ? "Brisk" : "Speed-clicked";
    }
}
