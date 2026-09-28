package com.playhavior.web;

import com.playhavior.entity.LearningPathway;
import com.playhavior.entity.Platform;
import com.playhavior.entity.Player;
import com.playhavior.repository.PlatformRepository;
import com.playhavior.service.CurrentPlayerService;
import com.playhavior.service.PlayhaviorWorkflowService;
import com.playhavior.service.ViolationInputValidator;
import com.playhavior.web.form.ViolationInputForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Handles the "Submit Your Notice" page (web layer).
 *
 * FLOW: steps 2, 3 and 6. Shows the form, receives the submission,
 *       hands off to the service layer, then redirects to the new pathway.
 * CALLS: ViolationInputValidator, CurrentPlayerService,
 *        PlayhaviorWorkflowService, PlatformRepository.
 * WHY: controllers stay thin. No business rules live here;
 *      they are in the service package.
 */
@Controller
public class ViolationIntakeController {

    // ===== DEPENDENCIES =====
    // WHY: constructor injection. Spring passes these beans in;
    //      final means they can never be missing or swapped.
    private final PlayhaviorWorkflowService workflowService;
    private final ViolationInputValidator inputValidator;
    private final CurrentPlayerService currentPlayerService;
    private final PlatformRepository platformRepository;

    public ViolationIntakeController(
            PlayhaviorWorkflowService workflowService,
            ViolationInputValidator inputValidator,
            CurrentPlayerService currentPlayerService,
            PlatformRepository platformRepository
    ) {
        this.workflowService = workflowService;
        this.inputValidator = inputValidator;
        this.currentPlayerService = currentPlayerService;
        this.platformRepository = platformRepository;
    }

    // ===== DATA FOR EVERY PAGE IN THIS CONTROLLER =====

    /**
     * Adds the active platforms to the Model as "platforms" (the Platform dropdown).
     * WHY: @ModelAttribute runs before EVERY request method in this controller, so the
     *      dropdown is filled on first load AND when errors redisplay the form.
     */
    @ModelAttribute("platforms")
    public List<Platform> supportedPlatforms() {
        return platformRepository
                .findByActiveTrueOrderByDisplayNameAsc();
    }

    // ===== REQUEST HANDLERS =====

    /**
     * GET /notices/new: shows the empty notice form (violation-intake.html).
     * FLOW: step 2.  CALLED BY: the browser, and the redirect from HomeController.
     * WHY the empty object: th:object and th:field need a form object to read,
     *     even when every field is blank. The containsAttribute check keeps a
     *     form that is already in the Model instead of replacing it.
     */
    @GetMapping("/notices/new")
    public String showForm(Model model) {
        if (!model.containsAttribute("violationForm")) {
            model.addAttribute(
                    "violationForm",
                    new ViolationInputForm()
            );
        }

        return "violation-intake";
    }

    /**
     * POST /notices: receives the submitted notice form.
     * FLOW: step 3 (validate), steps 4-5 (via the service), step 6 (redirect).
     * CALLS: ViolationInputValidator.validate(),
     *        CurrentPlayerService.requireCurrentPlayer(),
     *        PlayhaviorWorkflowService.startWorkflow().
     * Q: What if JavaScript is off? @Valid and the validator still run here, on the server.
     */
    @PostMapping("/notices")
    public String submitForm(
            @Valid                              // WHY: checks the annotations on ViolationInputForm
            @ModelAttribute("violationForm")    // WHY: data binding, fields copied in by name
            ViolationInputForm form,
            BindingResult bindingResult         // WHY: must come right after the form, or errors throw
    ) {
        // 1. Cross-field rules that annotations cannot express
        inputValidator.validate(
                form,
                bindingResult
        );

        // 2. Any error: show the same page again. The player's answers are
        //    still in the form object, so th:field refills every input.
        if (bindingResult.hasErrors()) {
            return "violation-intake";
        }

        // 3. Who is submitting (the seeded demo player until login exists)
        Player player =
                currentPlayerService.requireCurrentPlayer();

        // 4. Hand off to the service layer: decide, then save everything
        LearningPathway pathway =
                workflowService.startWorkflow(
                        player,
                        form
                );

        // 5. Post/Redirect/Get. WHY: a browser refresh cannot resubmit the
        //    form and create a duplicate pathway.
        return "redirect:/pathways/"
                + pathway.getPathwayId();
    }
}