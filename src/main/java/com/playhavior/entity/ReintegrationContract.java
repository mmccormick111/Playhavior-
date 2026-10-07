package com.playhavior.entity;

import com.playhavior.model.SentimentLabel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The player's Probationary Contract (framework Phase 4) (table: reintegration_contracts).
 * Holds the guardrails they chose for their return and their written reflection,
 * plus the sentiment analysis of that reflection.
 *
 * RELATIONSHIP: one contract -> one LearningPathway (@OneToOne, owns pathway_id).
 * CREATED BY: ModuleProgressService when the PROBATIONARY_CONTRACT module is completed.
 * READ BY: AppealReportService -> the appeal packet (Completion page + JSON export).
 */
@Entity
@Table(name = "reintegration_contracts")
public class ReintegrationContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pathway_id", nullable = false, unique = true)
    private LearningPathway pathway;

    // The pledge keys the player switched on (texts live in ModuleContentCatalog).
    // @ElementCollection: a simple list of values stored in its own table,
    // contract_pledges(contract_id, pledge_key), with no entity class needed.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "contract_pledges",
            joinColumns = @JoinColumn(name = "contract_id")
    )
    @Column(name = "pledge_key", length = 60)
    private List<String> pledgeKeys = new ArrayList<>();

    @Column(name = "reflection_text", nullable = false, length = 4000)
    private String reflectionText;

    // ===== SENTIMENT ALIGNMENT (from ReflectionSentimentAnalyzer) =====

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment_label", nullable = false, length = 20)
    private SentimentLabel sentimentLabel;

    // -1.0 (fully defensive) to +1.0 (fully accountable)
    @Column(name = "sentiment_score", nullable = false)
    private double sentimentScore;

    @Column(name = "accountable_signals", nullable = false)
    private int accountableSignals;

    @Column(name = "defensive_signals", nullable = false)
    private int defensiveSignals;

    @Column(name = "signed_at", nullable = false)
    private LocalDateTime signedAt;

    // Required by Hibernate: it creates an empty object, then fills it from the row
    public ReintegrationContract() {
    }

    public Long getContractId() {
        return contractId;
    }

    public LearningPathway getPathway() {
        return pathway;
    }

    public void setPathway(LearningPathway pathway) {
        this.pathway = pathway;
    }

    public List<String> getPledgeKeys() {
        return pledgeKeys;
    }

    public void setPledgeKeys(List<String> pledgeKeys) {
        this.pledgeKeys = pledgeKeys;
    }

    public String getReflectionText() {
        return reflectionText;
    }

    public void setReflectionText(String reflectionText) {
        this.reflectionText = reflectionText;
    }

    public SentimentLabel getSentimentLabel() {
        return sentimentLabel;
    }

    public void setSentimentLabel(SentimentLabel sentimentLabel) {
        this.sentimentLabel = sentimentLabel;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public int getAccountableSignals() {
        return accountableSignals;
    }

    public void setAccountableSignals(int accountableSignals) {
        this.accountableSignals = accountableSignals;
    }

    public int getDefensiveSignals() {
        return defensiveSignals;
    }

    public void setDefensiveSignals(int defensiveSignals) {
        this.defensiveSignals = defensiveSignals;
    }

    public LocalDateTime getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(LocalDateTime signedAt) {
        this.signedAt = signedAt;
    }
}
