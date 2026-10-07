package com.playhavior.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Decides where the site starts (web layer).
 * FLOW: step 1. localhost:8080/ redirects to the player's dashboard.
 */
@Controller
public class HomeController {

    /*
     * GET /: the app starts at the dashboard. With no case yet, the dashboard
     * shows a "Submit your first notice" button.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
