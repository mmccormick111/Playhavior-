package com.playhavior.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "summary_reports")
public class SummaryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summary_id;

    public String narrative_text;
    public int verification_code;


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
