package com.riwi.codewordle.controller;

import com.riwi.codewordle.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MvcGameController {

    private final ThemeService themeService;

    /**
     * Display the main game page
     * @param model Model to pass data to the view
     * @return The game view name
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("themes", themeService.getAllThemes());
        return "index";
    }
}
