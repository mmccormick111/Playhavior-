package com.playhavior.service;

import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PathwayModule;
import com.playhavior.entity.Player;
import com.playhavior.entity.PlayerCase;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.repository.PlayerCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gathers everything the player's dashboard shows (service layer).
 *
 * CALLED BY: DashboardController.showDashboard()
 * CALLS: PlayerCaseRepository, LearningPathwayRepository
 * ACTIVE CASE: the player's newest case; its pathway drives the hero card,
 *     the stats and the "Your Learning Path" list.
 */
@Service
public class DashboardService {

    private final PlayerCaseRepository caseRepository;
    private final LearningPathwayRepository pathwayRepository;

    public DashboardService(
            PlayerCaseRepository caseRepository,
            LearningPathwayRepository pathwayRepository
    ) {
        this.caseRepository = caseRepository;
        this.pathwayRepository = pathwayRepository;
    }

    @Transactional(readOnly = true)
    public DashboardView buildDashboard(Player player) {
        // 1. The player's cases, newest first; the first one is the active case
        List<PlayerCase> cases = caseRepository.findByPlayerOrderByCaseIdDesc(player);
        PlayerCase activeCase = cases.isEmpty() ? null : cases.get(0);

        // 2. Every pathway the player has (for the streak), and the active one
        List<LearningPathway> pathways = cases.stream()
                .map(playerCase -> pathwayRepository.findByPlayerCase(playerCase).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        LearningPathway active = activeCase == null
                ? null
                : pathwayRepository.findByPlayerCase(activeCase).orElse(null);

        // 3. Time invested, rounded UP to whole minutes (any time counts)
        long minutes = active == null ? 0 : (active.getSecondsInvested() + 59) / 60;

        return new DashboardView(
                player.getDisplay_name(),
                initialsOf(player.getDisplay_name()),
                active,
                active == null ? null : active.getNextModule(),
                activeCase == null ? null : activeCase.getBanReport(),
                minutes,
                streakDays(pathways, LocalDate.now())
        );
    }

    /*
     * Consecutive days, ending today (or yesterday), on which the player completed
     * at least one module. Yesterday counts so the streak does not reset at midnight
     * before the player has had a chance to play today.
     */
    static int streakDays(List<LearningPathway> pathways, LocalDate today) {
        Set<LocalDate> activeDays = pathways.stream()
                .flatMap(pathway -> pathway.getModules().stream())
                .map(PathwayModule::getCompletedAt)
                .filter(Objects::nonNull)
                .map(completedAt -> completedAt.toLocalDate())
                .collect(Collectors.toSet());

        LocalDate day = activeDays.contains(today) ? today : today.minusDays(1);
        int streak = 0;
        while (activeDays.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }

    // "Jordan Lee" -> "JL"; also used for the avatar in the nav bar
    public static String initialsOf(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return "?";
        }
        return Arrays.stream(displayName.trim().split("\\s+"))
                .limit(2)
                .map(word -> word.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
    }
}
