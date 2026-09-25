package com.playhavior.service;

import com.playhavior.entity.PathwayModule;
import com.playhavior.model.ModuleStatus;
import com.playhavior.model.PathwayMode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * Builds the list of modules for a new learning pathway.
 *
 * PLACEHOLDER CONTENT: the titles, lesson counts, and time
 * estimates below are stand-ins until the teaching approach
 * has been researched. Replace buildPlan() when real module
 * content is ready; nothing else needs to change.
 */
@Service
public class ModulePlanService {

    private static final int PLACEHOLDER_LESSONS_PER_MODULE = 3;
    private static final int PLACEHOLDER_MINUTES_PER_MODULE = 15;

    public List<PathwayModule> buildPlan(
            PathwayMode pathwayMode,
            String pathwayTitle,
            String platformName
    ) {
        List<String[]> outline = new ArrayList<>();

        outline.add(new String[] {
                "Understanding " + platformName + " Community Standards",
                "What the platform's rules say and why they exist."
        });
        outline.add(new String[] {
                "Core Concepts: " + pathwayTitle,
                "Placeholder module focused on the behavior in your notice."
        });
        outline.add(new String[] {
                "The Impact on Other Players",
                "Placeholder module on how the behavior affects the community."
        });

        if (pathwayMode == PathwayMode.REINSTATEMENT_SUPPORT) {
            outline.add(new String[] {
                    "Accountability and Reflection",
                    "Placeholder module on taking ownership of the violation."
            });
            outline.add(new String[] {
                    "Rejoining the Community",
                    "Placeholder module on positive habits going forward."
            });
        }

        List<PathwayModule> modules = new ArrayList<>();

        for (int i = 0; i < outline.size(); i++) {
            modules.add(new PathwayModule(
                    i + 1,
                    outline.get(i)[0],
                    outline.get(i)[1],
                    PLACEHOLDER_LESSONS_PER_MODULE,
                    PLACEHOLDER_MINUTES_PER_MODULE,
                    i == 0 ? ModuleStatus.AVAILABLE : ModuleStatus.LOCKED
            ));
        }

        return modules;
    }
}
