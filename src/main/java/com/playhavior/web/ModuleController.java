package com.playhavior.web;

import com.playhavior.content.ModuleContentCatalog;
import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PathwayModule;
import com.playhavior.model.ModuleStatus;
import com.playhavior.model.ReintegrationTrack;
import com.playhavior.model.RiskLevel;
import com.playhavior.service.InvalidSubmissionException;
import com.playhavior.service.ModuleProgressService;
import com.playhavior.service.ModuleSubmission;
import com.playhavior.service.PlatformPolicyService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * The interactive module pages (web layer).
 *
 * URLS:
 *   GET  /pathways/{pathwayId}/modules/{moduleId}           -> the module page
 *   POST /pathways/{pathwayId}/modules/{moduleId}/complete  -> grade + unlock next
 * CALLS: ModuleProgressService (open/grade), ModuleContentCatalog (content to show),
 *        PlatformPolicyService (the rule cited in the Validation Hub).
 * TEMPLATE: chosen by the module's type (ModuleType.getTemplateName()).
 */
@Controller
public class ModuleController {

    private final ModuleProgressService progressService;
    private final ModuleContentCatalog catalog;
    private final PlatformPolicyService policyService;

    public ModuleController(
            ModuleProgressService progressService,
            ModuleContentCatalog catalog,
            PlatformPolicyService policyService
    ) {
        this.progressService = progressService;
        this.catalog = catalog;
        this.policyService = policyService;
    }

    // ===== SHOW A MODULE =====

    /**
     * Opens a module (starting its clock) and adds the content its template needs.
     * A COMPLETED module opens in review mode: same content, no submit button.
     * WHY @Transactional: the template reads LAZY links (pathway -> case -> report).
     */
    @GetMapping("/pathways/{pathwayId}/modules/{moduleId}")
    @Transactional
    public String showModule(
            @PathVariable Long pathwayId,
            @PathVariable Long moduleId,
            Model model
    ) {
        PathwayModule module = progressService.openModule(pathwayId, moduleId);
        LearningPathway pathway = module.getPathway();
        ReintegrationTrack track = pathway.getTrack();

        // Shared by every module page
        model.addAttribute("pathway", pathway);
        model.addAttribute("module", module);
        model.addAttribute("banReport", pathway.getPlayerCase().getBanReport());
        model.addAttribute("reviewMode", module.getStatus() == ModuleStatus.COMPLETED);

        // Content for this module type only
        switch (module.getModuleType()) {
            case VALIDATION_HUB -> model.addAttribute("policyRules",
                    pathway.getPlatformPolicy() == null
                            ? List.of()
                            : policyService.findRulesFor(pathway.getPlatformPolicy(),
                                    pathway.getViolationCategory()));
            case PERSPECTIVE_SHIFT -> {
                model.addAttribute("rounds", catalog.chatRounds());
                model.addAttribute("startingBar", ModuleContentCatalog.STARTING_BAR_VALUE);
            }
            case RISK_MATRIX -> {
                model.addAttribute("scenarios", catalog.riskScenarios());
                model.addAttribute("riskLevels", RiskLevel.values());
                model.addAttribute("narration", ModuleContentCatalog.RISK_MATRIX_NARRATION);
            }
            case ACCOUNTABILITY_SANDBOX -> {
                model.addAttribute("segments", catalog.sandboxSegments(track));
                model.addAttribute("secondsPerDecision", ModuleContentCatalog.SANDBOX_SECONDS_PER_DECISION);
                model.addAttribute("pointsFirstTry", ModuleContentCatalog.POINTS_FIRST_TRY);
                model.addAttribute("pointsAfterRetry", ModuleContentCatalog.POINTS_AFTER_RETRY);
            }
            case PROBATIONARY_CONTRACT -> {
                model.addAttribute("pledges", catalog.pledges(track));
                model.addAttribute("minPledges", ModuleContentCatalog.MIN_PLEDGES);
                model.addAttribute("minReflection", ModuleContentCatalog.MIN_REFLECTION_LENGTH);
                model.addAttribute("reflectionPrompt", catalog.reflectionPrompt(track));
            }
        }

        return module.getModuleType().getTemplateName();
    }

    // ===== COMPLETE A MODULE =====

    /**
     * Grades the posted answers. Passed -> back to the pathway (or the appeal packet
     * when everything is done). Not passed -> back to the module with the reason.
     * WHY @RequestParam MultiValueMap: each module posts different field names
     *     (answer_round1, attempts_seg2, pledges...), so we take them all.
     * WHY redirect + flash attribute: Post/Redirect/Get, with a one-time message.
     */
    @PostMapping("/pathways/{pathwayId}/modules/{moduleId}/complete")
    public String completeModule(
            @PathVariable Long pathwayId,
            @PathVariable Long moduleId,
            @RequestParam MultiValueMap<String, String> params,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LearningPathway pathway = progressService.completeModule(
                    pathwayId, moduleId, ModuleSubmission.from(params));

            if (pathway.isFinished()) {
                redirectAttributes.addFlashAttribute("flash",
                        "Pathway complete. Your results are packaged below.");
                return "redirect:/pathways/" + pathwayId + "/report";
            }

            redirectAttributes.addFlashAttribute("flash",
                    "Module complete. The next module is now unlocked.");
            return "redirect:/pathways/" + pathwayId;

        } catch (InvalidSubmissionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pathways/" + pathwayId + "/modules/" + moduleId;
        }
    }
}
