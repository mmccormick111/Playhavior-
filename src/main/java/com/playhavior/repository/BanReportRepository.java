package com.playhavior.repository;

import com.playhavior.entity.BanReport;
import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for BanReport. USED BY: PlayhaviorWorkflowService (save, FLOW step 5). */
public interface BanReportRepository extends JpaRepository<BanReport, Long> {
}
