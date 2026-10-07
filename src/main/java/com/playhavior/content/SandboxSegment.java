package com.playhavior.content;

import java.util.List;

/**
 * One of the three user-paced decision points of the Accountability Sandbox
 * (Segmenting Principle, framework Phase 3): setup -> catalyst -> timed choice.
 */
public record SandboxSegment(
        String key,
        String title,
        String setup,
        String catalyst,
        List<SandboxChoice> choices
) {
}
