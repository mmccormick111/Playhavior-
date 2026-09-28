package com.playhavior.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/*
 * Links a player to one submitted ban report: one "case file" (table: player_case).
 * (Named PlayerCase because "CASE" is a reserved word in SQL/JPQL.)
 *
 * RELATIONSHIPS:
 *   - many cases -> one Player (@ManyToOne, player_id): a player can be banned again
 *   - one case   -> one BanReport (@OneToOne, report_id, unique)
 *   - one LearningPathway points back here (its case_id)
 * CREATED BY: PlayhaviorWorkflowService.buildCase() (FLOW step 5)
 */
@Entity
@Table(name = "player_case")
public class PlayerCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long caseId;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "report_id",
            nullable = false,
            unique = true
    )
    private BanReport banReport;

    // Required by Hibernate: it creates an empty object, then fills it from the row
    public PlayerCase() {
    }

    public Long getCaseId() {
        return caseId;
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BanReport getBanReport() {
        return banReport;
    }

    public void setBanReport(BanReport banReport) {
        this.banReport = banReport;
    }
}
