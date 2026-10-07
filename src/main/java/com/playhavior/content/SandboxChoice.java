package com.playhavior.content;

/** One option at a decision point of the Accountability Sandbox. */
public record SandboxChoice(
        String key,
        String text,
        boolean compliant,      // restorative / compliant = progresses and earns points
        String feedback         // Coherence Principle: one clear reason, nothing extra
) {
}
