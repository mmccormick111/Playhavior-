package com.playhavior.repository;

import com.playhavior.entity.SummaryReport;
import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for SummaryReport. USED BY: PlayhaviorWorkflowService.completePathway() (not called yet). */
public interface SummaryReportRepository extends JpaRepository<SummaryReport, Long> {
}
