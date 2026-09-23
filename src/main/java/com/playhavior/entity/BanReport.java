package com.playhavior.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ban_reports")
public class BanReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long report_id;

    private String stated_reason;
    private String platform;


    public BanReport() {
    }

    public Long getReport_id(){
        return report_id;
    }

    public String getStated_reason(){
        return stated_reason;
    }

    public void setStated_reason(String stated_reason) {
        this.stated_reason = stated_reason;
    }

    public String getPlatform(){
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
