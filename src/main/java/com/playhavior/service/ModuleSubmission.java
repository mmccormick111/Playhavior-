package com.playhavior.service;

import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * What a module page posts back, parsed from the raw form parameters.
 *
 *   answer_<questionKey>   = the option the player finally chose
 *   attempts_<questionKey> = how many tries it took (1 = right first time)
 *   pledges                = chosen pledge keys (contract only, may repeat)
 *   reflection             = the written reflection (contract only)
 *
 * NOTE: attempt counts come from the browser, so they could be edited; the server
 *       still re-checks every FINAL answer. Fine for a prototype; a production build
 *       would record each attempt server-side.
 */
public record ModuleSubmission(
        Map<String, String> answers,
        Map<String, Integer> attempts,
        List<String> pledges,
        String reflection
) {

    private static final int MAX_ATTEMPTS = 20;

    public static ModuleSubmission from(MultiValueMap<String, String> params) {
        Map<String, String> answers = new HashMap<>();
        Map<String, Integer> attempts = new HashMap<>();

        params.forEach((name, values) -> {
            String value = values.isEmpty() ? null : values.get(0);
            if (name.startsWith("answer_")) {
                answers.put(name.substring("answer_".length()), value);
            } else if (name.startsWith("attempts_")) {
                attempts.put(name.substring("attempts_".length()), parseAttempts(value));
            }
        });

        List<String> pledges = params.getOrDefault("pledges", List.of());
        String reflection = params.getFirst("reflection");

        return new ModuleSubmission(answers, attempts, pledges, reflection);
    }

    // How many tries this question took; anything missing or invalid counts as 1
    public int attemptsFor(String questionKey) {
        return attempts.getOrDefault(questionKey, 1);
    }

    // Clamp to 1..20 so a bad value cannot break the statistics
    private static int parseAttempts(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(1, Math.min(MAX_ATTEMPTS, parsed));
        } catch (NumberFormatException | NullPointerException e) {
            return 1;
        }
    }
}
