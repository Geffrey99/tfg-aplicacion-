package com.webG.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index"; // Esto busca templates/index.html
    }
}
