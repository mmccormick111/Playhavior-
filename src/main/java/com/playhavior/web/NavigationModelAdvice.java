package com.playhavior.web;

import com.playhavior.service.CurrentPlayerService;
import com.playhavior.service.DashboardService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds data the shared nav bar needs to EVERY page (web layer).
 * WHY @ControllerAdvice: its @ModelAttribute methods run before every controller
 *     method, so fragments/layout.html can show ${navInitials} on all pages
 *     without each controller adding it.
 */
@ControllerAdvice
public class NavigationModelAdvice {

    private final CurrentPlayerService currentPlayerService;

    public NavigationModelAdvice(CurrentPlayerService currentPlayerService) {
        this.currentPlayerService = currentPlayerService;
    }

    // The avatar initials, e.g. "JL" for the demo player Jordan Lee
    @ModelAttribute("navInitials")
    public String navInitials() {
        return currentPlayerService.findCurrentPlayer()
                .map(player -> DashboardService.initialsOf(player.getDisplay_name()))
                .orElse("?");
    }
}
