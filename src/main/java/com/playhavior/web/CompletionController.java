package com.playhavior.web;

import com.playhavior.entity.LearningPathway;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.service.AppealReport;
import com.playhavior.service.AppealReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

/**
 * The appeal packet / Completion page (framework Phase 4 data export) (web layer).
 *
 * URLS:
 *   GET /completion                      -> the newest pathway's packet (nav link)
 *   GET /pathways/{pathwayId}/report      -> the packet as a page (report.html)
 *   GET /pathways/{pathwayId}/report.json -> the same packet as JSON, for the
 *                                            platform's moderation / support team
 * CALLS: LearningPathwayRepository, AppealReportService.
 */
@Controller
public class CompletionController {

    private final LearningPathwayRepository pathwayRepository;
    private final AppealReportService reportService;

    public CompletionController(
            LearningPathwayRepository pathwayRepository,
            AppealReportService reportService
    ) {
        this.pathwayRepository = pathwayRepository;
        this.reportService = reportService;
    }

    // "Completion" nav link: newest pathway's packet, or the notice form if none exists
    @GetMapping("/completion")
    public String showLatestReport() {
        return pathwayRepository.findFirstByOrderByGeneratedAtDesc()
                .map(pathway -> "redirect:/pathways/" + pathway.getPathwayId() + "/report")
                .orElse("redirect:/notices/new");
    }

    @GetMapping("/pathways/{pathwayId}/report")
    @Transactional(readOnly = true)
    public String showReport(@PathVariable Long pathwayId, Model model) {
        model.addAttribute("report", reportService.build(findPathway(pathwayId)));
        return "report";
    }

    /**
     * The export. @ResponseBody: the returned record is written as JSON (by Jackson)
     * instead of being treated as a template name.
     */
    @GetMapping(value = "/pathways/{pathwayId}/report.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public AppealReport exportReport(@PathVariable Long pathwayId) {
        return reportService.build(findPathway(pathwayId));
    }

    private LearningPathway findPathway(Long pathwayId) {
        return pathwayRepository.findById(pathwayId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Learning pathway not found."));
    }
}
