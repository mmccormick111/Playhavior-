package com.playhavior.service;

import com.playhavior.model.DurationUnit;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.PenaltyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PenaltyEligibilityService {

    private final long minimumReviewDays;

    public PenaltyEligibilityService(
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
        if (penaltyType == PenaltyType.PERMANENT_BAN) {
            return PathwayMode.REINSTATEMENT_SUPPORT;
        }

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

        return PathwayMode.EDUCATIONAL_ONLY;
    }

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
