package com.playhavior.service;

import com.playhavior.entity.BanReport;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PathwayModule;

/**
 * Exactly what the dashboard page needs, built by DashboardService.
 * WHY a record: the template reads simple values; all the work of finding them
 *     happens in the service, so the controller and template stay simple.
 */
public record DashboardView(
        String displayName,
        String initials,
        LearningPathway pathway,      // null when the player has no case yet
        PathwayModule nextModule,     // null when there is no pathway or it is finished
        BanReport banReport,          // the active case's notice, or null
        long minutesInvested,
        int streakDays
) {

    // th:if="${dashboard.hasPathway()}" switches between the dashboard and the empty state
    public boolean hasPathway() {
        return pathway != null;
    }
}
