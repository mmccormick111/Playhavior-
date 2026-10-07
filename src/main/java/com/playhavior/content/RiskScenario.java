package com.playhavior.content;

import com.playhavior.model.RiskLevel;

/** One grey-market scenario the player sorts into the Risk Matrix (framework Phase 2B). */
public record RiskScenario(
        String key,
        String prompt,
        RiskLevel correctLevel,
        String explanation      // shown after a choice: WHY it belongs in that column
) {
}
