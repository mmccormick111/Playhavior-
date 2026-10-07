package com.playhavior.content;

/** One message in a simulated chat feed (Perspective Shift module). */
public record ChatLine(
        String speaker,
        String message,
        boolean toxic      // true = highlighted in red, so the player sees the harm
) {
}
