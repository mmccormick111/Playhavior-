package com.playhavior.model;

/**
 * Result of ReflectionSentimentAnalyzer on the player's written reflection
 * ("Sentiment Alignment" in the appeal packet).
 */
public enum SentimentLabel {

    ACCOUNTABLE("Accountable language"),
    MIXED("Mixed"),
    DEFENSIVE("Defensive / evasive language");

    private final String label;

    SentimentLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
