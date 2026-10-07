package com.playhavior.content;

import java.util.List;

/** One round of the Perspective-Taking Simulator (Social track, framework Phase 2A). */
public record ChatRound(
        String key,
        String title,
        String scene,           // who the player is and what just happened
        List<ChatLine> feed,
        String prompt,
        List<ChatOption> options
) {
}
