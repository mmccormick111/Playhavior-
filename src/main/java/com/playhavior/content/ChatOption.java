package com.playhavior.content;

/**
 * One response the player can pick from the drop-down in a chat round.
 * moraleDelta / performanceDelta move the on-screen "Morale" and "Team Performance"
 * bars, so the player SEES how their words change the match.
 */
public record ChatOption(
        String key,
        String text,
        boolean constructive,   // only constructive options let the player advance
        int moraleDelta,
        int performanceDelta,
        String feedback
) {
}
