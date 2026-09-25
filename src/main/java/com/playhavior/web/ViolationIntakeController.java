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

@Controller
public class ViolationIntakeController {

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

    @ModelAttribute("platforms")
    public List<Platform> supportedPlatforms() {
        return platformRepository
                .findByActiveTrueOrderByDisplayNameAsc();
    }

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

    @PostMapping("/notices")
    public String submitForm(
            @Valid
            @ModelAttribute("violationForm")
            ViolationInputForm form,
            BindingResult bindingResult
    ) {
        inputValidator.validate(
                form,
                bindingResult
        );

        if (bindingResult.hasErrors()) {
            return "violation-intake";
        }

        Player player =
                currentPlayerService.requireCurrentPlayer();

        LearningPathway pathway =
                workflowService.startWorkflow(
                        player,
                        form
                );

        return "redirect:/pathways/"
                + pathway.getPathway_ID();
    }
}