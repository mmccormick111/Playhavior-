package com.playhavior.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /*
     * The demo starts at the notice form. Point this at a
     * dashboard once sign-up/login and the dashboard exist.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/notices/new";
    }
}
