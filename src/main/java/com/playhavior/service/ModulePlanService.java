package com.playhavior.service;

import com.playhavior.content.ModuleContentCatalog;
import com.playhavior.entity.PathwayModule;
import com.playhavior.model.ModuleStatus;
import com.playhavior.model.ModuleType;
import com.playhavior.model.PathwayMode;
import com.playhavior.model.ReintegrationTrack;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the list of modules for a new learning pathway, following the
 * four-phase accountability framework (service layer).
 *
 *   1. Validation Hub              (Phase 1, every pathway)
 *   2. Perspective Shift  OR  Risk Matrix  (Phase 2, chosen by the TRACK)
 *   3. Accountability Sandbox      (Phase 3, every pathway)
 *   4. Probationary Contract       (Phase 4, REINSTATEMENT_SUPPORT only:
 *                                   it packages the appeal for the platform)
 *
 * FLOW: step 5.  CALLED BY: PlayhaviorWorkflowService.buildLearningPathway()
 * CALLS: ModuleContentCatalog.stepCount() for each module's number of activities.
 */
@Service
public class ModulePlanService {

    private final ModuleContentCatalog catalog;

    public ModulePlanService(ModuleContentCatalog catalog) {
        this.catalog = catalog;
    }

    public List<PathwayModule> buildPlan(
            PathwayMode pathwayMode,
            ReintegrationTrack track,
            String platformName
    ) {
        List<PathwayModule> modules = new ArrayList<>();

        // Phase 1: face the exact cause of the restriction
        modules.add(module(ModuleType.VALIDATION_HUB, track,
                "Face Your Notice: The Validation Hub",
                "Review the exact behavior behind your " + platformName
                        + " restriction and confirm it before you begin.",
                5));

        // Phase 2: the track-specific module
        if (track == ReintegrationTrack.SYSTEM_INTEGRITY) {
            modules.add(module(ModuleType.RISK_MATRIX, track,
                    "System Integrity: The Risk Matrix",
                    "Learn how cheats, exploits and phishing damage the game, then sort "
                            + "grey-market scenarios from legitimate play to permanent-ban risk.",
                    12));
        } else {
            modules.add(module(ModuleType.PERSPECTIVE_SHIFT, track,
                    "Perspective Shift: The Teammate's Seat",
                    "Step into a live match and watch how each chat message changes "
                            + "your team's morale and performance.",
                    12));
        }

        // Phase 3: the branching, timed assessment
        modules.add(module(ModuleType.ACCOUNTABILITY_SANDBOX, track,
                "The Accountability Sandbox",
                "Three timed, match-deciding decisions. Restorative choices earn "
                        + "Standing Points; harmful ones restart the segment.",
                10));

        // Phase 4: only appeal-support pathways produce a packet for the platform
        if (pathwayMode == PathwayMode.REINSTATEMENT_SUPPORT) {
            modules.add(module(ModuleType.PROBATIONARY_CONTRACT, track,
                    "Probationary Contract & Appeal Packet",
                    "Set your own guardrails for your return, reflect in your own words, "
                            + "and package your results for " + platformName + "'s review team.",
                    8));
        }

        // Number the modules in order. WHY: only module 1 starts AVAILABLE;
        // the rest unlock one at a time as each is completed.
        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).setModuleOrder(i + 1);
            modules.get(i).setStatus(i == 0 ? ModuleStatus.AVAILABLE : ModuleStatus.LOCKED);
        }

        return modules;
    }

    // Builds one module; order and status are set in buildPlan()
    private PathwayModule module(
            ModuleType type,
            ReintegrationTrack track,
            String title,
            String description,
            int estimatedMinutes
    ) {
        return new PathwayModule(
                type,
                0,
                title,
                description,
                catalog.stepCount(type, track),
                estimatedMinutes,
                ModuleStatus.LOCKED
        );
    }
}
