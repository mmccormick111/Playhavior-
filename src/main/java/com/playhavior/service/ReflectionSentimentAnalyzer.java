package com.playhavior.service;

import com.playhavior.model.SentimentLabel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Scores the player's written reflection for "Sentiment Alignment" (framework Phase 4):
 * does it use accountable language, or defensive / evasive language?
 *
 * CALLED BY: ModuleProgressService when the Probationary Contract is submitted.
 * HOW: counts known accountable phrases vs defensive phrases (a transparent keyword
 *      heuristic, so a moderator can see exactly why a label was given).
 * LIMITATION: keyword matching misses sarcasm and context; a trained language model
 *      could replace analyze() later without changing any caller.
 */
@Service
public class ReflectionSentimentAnalyzer {

    // Phrases that take ownership, name the harm, or commit to change
    private static final List<String> ACCOUNTABLE_PHRASES = List.of(
            "i was wrong", "my fault", "my mistake", "i take responsibility",
            "i'm responsible", "i am responsible", "responsibility", "i should have",
            "i shouldn't have", "i should not have", "i understand", "i regret",
            "i'm sorry", "i am sorry", "i apologize", "i apologise", "i hurt",
            "it hurt", "harm", "affected", "i learned", "i've learned", "i will",
            "i'll", "going forward", "next time", "own it", "accountable"
    );

    // Phrases that deny, minimise or shift blame
    private static final List<String> DEFENSIVE_PHRASES = List.of(
            "not my fault", "wasn't my fault", "unfair", "everyone does it",
            "everybody does it", "they started", "he started", "she started",
            "wasn't me", "false ban", "falsely banned", "overreact", "just a joke",
            "only joking", "no big deal", "whatever", "deserved it", "snowflake",
            "too sensitive", "can't take a joke", "didn't do anything"
    );

    public SentimentResult analyze(String reflection) {
        String text = reflection == null
                ? ""
                : reflection.toLowerCase(Locale.ROOT);

        int accountable = countMatches(text, ACCOUNTABLE_PHRASES);
        int defensive = countMatches(text, DEFENSIVE_PHRASES);

        // Score from -1.0 (only defensive) to +1.0 (only accountable); 0 when no signals
        int total = accountable + defensive;
        double score = total == 0
                ? 0.0
                : (double) (accountable - defensive) / total;

        SentimentLabel label;
        if (defensive > accountable) {
            label = SentimentLabel.DEFENSIVE;
        } else if (accountable >= 2 && accountable > defensive) {
            label = SentimentLabel.ACCOUNTABLE;
        } else {
            label = SentimentLabel.MIXED;
        }

        return new SentimentResult(label, score, accountable, defensive);
    }

    private int countMatches(String text, List<String> phrases) {
        return (int) phrases.stream()
                .filter(text::contains)
                .count();
    }

    /** The analysis result, stored on ReintegrationContract. */
    public record SentimentResult(
            SentimentLabel label,
            double score,
            int accountableSignals,
            int defensiveSignals
    ) {
    }
}
