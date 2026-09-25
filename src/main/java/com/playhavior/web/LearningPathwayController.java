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

@Controller
public class LearningPathwayController {

    private final LearningPathwayRepository pathwayRepository;
    private final PlatformPolicyService policyService;

    public LearningPathwayController(
            LearningPathwayRepository pathwayRepository,
            PlatformPolicyService policyService
    ) {
        this.pathwayRepository = pathwayRepository;
        this.policyService = policyService;
    }

    /*
     * "My Pathway" nav link: opens the most recently generated
     * pathway, or sends the player to the notice form if none exists.
     */
    @GetMapping("/pathways/latest")
    public String showLatestPathway() {
        return pathwayRepository.findFirstByOrderByGeneratedAtDesc()
                .map(pathway -> "redirect:/pathways/" + pathway.getPathwayId())
                .orElse("redirect:/notices/new");
    }

    @GetMapping("/pathways/{pathwayId}")
    @Transactional(readOnly = true)
    public String showPathway(
            @PathVariable Long pathwayId,
            Model model
    ) {
        LearningPathway pathway =
                pathwayRepository.findById(pathwayId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Learning pathway not found."
                                )
                        );

        List<PolicyRule> policyRules =
                pathway.getPlatformPolicy() == null
                        ? List.of()
                        : policyService.findRulesFor(
                        pathway.getPlatformPolicy(),
                        pathway.getViolationCategory()
                );

        model.addAttribute("pathway", pathway);
        model.addAttribute("banReport", pathway.getPlayerCase().getBanReport());
        model.addAttribute("policyRules", policyRules);

        return "pathway-details";
    }
}
