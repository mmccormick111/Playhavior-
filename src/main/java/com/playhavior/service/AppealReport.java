package com.playhavior.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The appeal packet for one pathway (framework Phase 4, "Application Decision Dashboard").
 * Everything the platform's moderation team needs, in one immutable object.
 *
 * BUILT BY: AppealReportService.build()
 * SHOWN BY: report.html (Completion page) and exported as JSON at /pathways/{id}/report.json
 */
public record AppealReport(
        Long pathwayId,
        String playerName,
        String platform,
        String gameTitle,
        String statedReason,
        String violationCategory,
        String track,
        String pathwayMode,
        String platformReferenceCode,   // lets the platform look up the original case
        LocalDate noticeDate,

        int completedModules,
        int totalModules,
        boolean appealPacket,           // false for EDUCATIONAL_ONLY pathways
        boolean readyForReview,         // every module done AND it is an appeal packet

        // ===== THE THREE DECISION METRICS =====
        Integer firstPassAccuracyPercent,   // null until a graded module is completed
        String engagementLabel,             // Engaged / Brisk / Speed-clicked / Not started
        long secondsInvested,
        long minimumEngagedSeconds,
        String sentimentLabel,              // null until the contract is signed
        Double sentimentScore,

        int standingPoints,
        List<String> pledges,
        String reflection,
        List<ModuleResult> modules,
        LocalDateTime generatedAt
) {

    /** One row of the per-module breakdown. */
    public record ModuleResult(
            int order,
            String title,
            String status,
            int questions,
            int firstPassCorrect,
            int attempts,
            long secondsSpent,
            int minimumSeconds,
            String engagement,
            int standingPoints
    ) {
    }
}
