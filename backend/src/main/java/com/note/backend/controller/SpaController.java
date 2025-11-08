package com.note.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/", "/profile", "/profile/{id}"})
    public String forwardToIndex() {
        // Forward SPA routes to index.html so client-side router can handle them
        return "forward:/index.html";
    }
}
