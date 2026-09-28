package com.playhavior.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The future completion report the player attaches to an appeal (table: summary_reports).
 * CREATED BY: PlayhaviorWorkflowService.completePathway(), which nothing calls yet.
 * TODO: link to LearningPathway (and its platform reference code) for the Completion page.
 */
@Entity
@Table(name = "summary_reports")
public class SummaryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summary_id;

    // TODO: make these fields private; public skips the getters/setters
    public String narrative_text;
    public int verification_code;


    // Required by Hibernate: it creates an empty object, then fills it from the row
    public SummaryReport() {
    }

    public Long getSummary_Id() {
        return summary_id;
    }

    public String getNarrative_text(){
        return narrative_text;
    }

    public void setNarrative_text(String narrative_text){
        this.narrative_text = narrative_text;
    }

    public int getVerification_code(){
        return verification_code;
    }

    public void setVerification_code(int verification_code) {
        this.verification_code = verification_code;
    }
}
