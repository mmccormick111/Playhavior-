package com.playhavior.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Decides where the site starts (web layer).
 * FLOW: step 1. localhost:8080/ redirects to the notice form.
 */
@Controller
public class HomeController {

    /*
     * GET /: the demo starts at the notice form.
     * TODO: point this at /dashboard once the dashboard exists.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/notices/new";
    }
}
