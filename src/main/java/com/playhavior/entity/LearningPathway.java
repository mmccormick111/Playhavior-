package com.playhavior.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "learning_pathways")
public class LearningPathway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pathway_id;
    public int total_modules;
    public String pathway_title;

    public LearningPathway() {
    }

    public Long getPathway_ID() {
        return pathway_id;
    }

    public int getTotal_modules(){
        return total_modules;
    }

    public void setTotal_modules(int total_modules) {
        this.total_modules = total_modules;
    }

    public String getPathway_title() {
        return pathway_title;
    }

    public void setPathway_title(String pathway_title) {
        this.pathway_title = pathway_title;
    }
}
