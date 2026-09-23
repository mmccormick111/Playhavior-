package com.playhavior.controller;

import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.SummaryReport;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.repository.SummaryReportRepository;
import com.playhavior.service.PlayhaviorWorkflowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DemoController {

    private final PlayhaviorWorkflowService workflowService;
    private final LearningPathwayRepository learningPathwayRepository;
    private final SummaryReportRepository summaryReportRepository;

    public DemoController(
            PlayhaviorWorkflowService workflowService,
            LearningPathwayRepository learningPathwayRepository,
            SummaryReportRepository summaryReportRepository) {

        this.workflowService = workflowService;
        this.learningPathwayRepository = learningPathwayRepository;
        this.summaryReportRepository = summaryReportRepository;
    }

    @GetMapping("/")
    public String showIntakeForm() {
        return "index";
    }

    @PostMapping("/workflow/start")
    public String startWorkflow(
            @RequestParam String displayName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String statedReason,
            @RequestParam String platform) {

        LearningPathway pathway = workflowService.startWorkflow(
                displayName,
                email,
                password,
                statedReason,
                platform);

        return "redirect:/pathway/" + pathway.getPathway_ID();
    }

    @GetMapping("/pathway/{pathwayId}")
    public String showPathway(@PathVariable Long pathwayId, Model model) {
        LearningPathway pathway = learningPathwayRepository.findById(pathwayId)
                .orElseThrow(() -> new IllegalArgumentException("Learning pathway not found"));

        model.addAttribute("pathway", pathway);
        return "pathway";
    }

    @PostMapping("/pathway/{pathwayId}/complete")
    public String completePathway(@PathVariable Long pathwayId) {
        SummaryReport summaryReport = workflowService.completePathway(pathwayId);

        return "redirect:/summary/" + summaryReport.getSummary_Id();
    }

    @GetMapping("/summary/{summaryId}")
    public String showSummary(@PathVariable Long summaryId, Model model) {
        SummaryReport summaryReport = summaryReportRepository.findById(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("Summary report not found"));

        model.addAttribute("summaryReport", summaryReport);
        return "summary";
    }
}
