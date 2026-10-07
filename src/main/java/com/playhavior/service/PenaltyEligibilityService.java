package com.playhavior.service;

import com.playhavior.model.DurationUnit;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.PenaltyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Decides the pathway MODE from the ban type and length (service layer).
 *
 * FLOW: step 4.  CALLED BY: PlayhaviorWorkflowService.startWorkflow()
 * RULES (in order):
 *   1. PERMANENT_BAN                                     -> REINSTATEMENT_SUPPORT
 *   2. TEMPORARY_BAN / ACCOUNT_SUSPENSION >= 7 days      -> REINSTATEMENT_SUPPORT
 *   3. anything else (shorter bans, restrictions, ...)   -> EDUCATIONAL_ONLY
 * RESULT: REINSTATEMENT_SUPPORT gets 4 modules (incl. the appeal packet), EDUCATIONAL_ONLY 3.
 * WHY the 7 lives in application.properties: a business rule you can tune
 *     without changing code.
 */
@Service
public class PenaltyEligibilityService {

    private final long minimumReviewDays;

    public PenaltyEligibilityService(
            // Reads playhavior.minimum-review-days; ":7" = default if the property is missing
            @Value("${playhavior.minimum-review-days:7}")
            long minimumReviewDays
    ) {
        this.minimumReviewDays = minimumReviewDays;
    }

    public PathwayMode determineMode(
            PenaltyType penaltyType,
            Integer durationAmount,
            DurationUnit durationUnit
    ) {
        // Rule 1
        if (penaltyType == PenaltyType.PERMANENT_BAN) {
            return PathwayMode.REINSTATEMENT_SUPPORT;
        }

        // Rule 2 (the validator has already made sure a duration exists)
        if (penaltyType == PenaltyType.TEMPORARY_BAN
                || penaltyType == PenaltyType.ACCOUNT_SUSPENSION) {

            if (durationAmount == null || durationUnit == null) {
                throw new IllegalArgumentException(
                        "Temporary penalties require a duration."
                );
            }

            Duration duration = convertDuration(
                    durationAmount,
                    durationUnit
            );

            if (duration.compareTo(
                    Duration.ofDays(minimumReviewDays)
            ) >= 0) {
                return PathwayMode.REINSTATEMENT_SUPPORT;
            }
        }

        // Rule 3
        return PathwayMode.EDUCATIONAL_ONLY;
    }

    // 2 WEEKS -> Duration of 14 days.
    // WHY: lengths can only be compared once they share a unit.
    // WHY 7L: long arithmetic, so a large number cannot overflow.
    private Duration convertDuration(
            int amount,
            DurationUnit unit
    ) {
        return switch (unit) {
            case HOURS -> Duration.ofHours(amount);
            case DAYS -> Duration.ofDays(amount);
            case WEEKS -> Duration.ofDays(amount * 7L);
        };
    }
}
