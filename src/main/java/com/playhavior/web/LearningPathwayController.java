package com.playhavior.web;

import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.PolicyRule;
import com.playhavior.repository.LearningPathwayRepository;
import com.playhavior.service.PlatformPolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Shows a generated learning pathway (web layer).
 *
 * FLOW: step 7. Loads the pathway and its policy citations for pathway-details.html.
 * CALLS: LearningPathwayRepository, PlatformPolicyService.
 */
@Controller
public class LearningPathwayController {

    // ===== DEPENDENCIES =====
    private final LearningPathwayRepository pathwayRepository;
    private final PlatformPolicyService policyService;

    public LearningPathwayController(
            LearningPathwayRepository pathwayRepository,
            PlatformPolicyService policyService
    ) {
        this.pathwayRepository = pathwayRepository;
        this.policyService = policyService;
    }

    // ===== REQUEST HANDLERS =====

    /*
     * GET /pathways/latest: the "My Pathway" nav link. Opens the most recently
     * generated pathway, or sends the player to the notice form if none exists.
     * WHY: the nav link has no pathway id to use until login exists.
     */
    @GetMapping("/pathways/latest")
    public String showLatestPathway() {
        return pathwayRepository.findFirstByOrderByGeneratedAtDesc()
                .map(pathway -> "redirect:/pathways/" + pathway.getPathwayId())
                .orElse("redirect:/notices/new");
    }

    /**
     * GET /pathways/{pathwayId}: the Your Learning Pathway page.
     * FLOW: step 7.  CALLED BY: the redirect from ViolationIntakeController.submitForm().
     * WHY @Transactional(readOnly = true): the pathway's links are LAZY, so reading
     *     playerCase.banReport needs an open database session.
     */
    @GetMapping("/pathways/{pathwayId}")
    @Transactional(readOnly = true)
    public String showPathway(
            @PathVariable Long pathwayId,
            Model model
    ) {
        // 1. Load the pathway, or answer 404.
        //    Q: Unknown id? -> a real 404 instead of a 500 error (there is a test for it).
        LearningPathway pathway =
                pathwayRepository.findById(pathwayId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Learning pathway not found."
                                )
                        );

        // 2. The platform rules for this category, for the "Standards Referenced" card
        List<PolicyRule> policyRules =
                pathway.getPlatformPolicy() == null
                        ? List.of()
                        : policyService.findRulesFor(
                        pathway.getPlatformPolicy(),
                        pathway.getViolationCategory()
                );

        // 3. Data the template reads: ${pathway}, ${banReport}, ${policyRules}
        model.addAttribute("pathway", pathway);
        model.addAttribute("banReport", pathway.getPlayerCase().getBanReport());
        model.addAttribute("policyRules", policyRules);

        return "pathway-details";
    }
}
