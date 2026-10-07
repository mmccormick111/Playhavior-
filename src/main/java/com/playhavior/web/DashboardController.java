package com.playhavior.web;

import com.playhavior.entity.Player;
import com.playhavior.service.CurrentPlayerService;
import com.playhavior.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The player's home page (web layer).
 *
 * FLOW: step 1 (HomeController redirects / here).
 * CALLS: CurrentPlayerService, DashboardService.
 * WHY so short: all the gathering happens in DashboardService (thin controller).
 */
@Controller
public class DashboardController {

    private final CurrentPlayerService currentPlayerService;
    private final DashboardService dashboardService;

    public DashboardController(
            CurrentPlayerService currentPlayerService,
            DashboardService dashboardService
    ) {
        this.currentPlayerService = currentPlayerService;
        this.dashboardService = dashboardService;
    }

    /** GET /dashboard: welcome card, stats and "Your Learning Path" (dashboard.html). */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Player player = currentPlayerService.requireCurrentPlayer();
        model.addAttribute("dashboard", dashboardService.buildDashboard(player));
        return "dashboard";
    }
}
