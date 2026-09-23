package com.playhavior.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_case")

public class Case {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long case_id;

    public String status;
    public String description;

    public Case(){
    }

    public Long getCase_id() {
        return case_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
