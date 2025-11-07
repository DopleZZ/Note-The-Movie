package com.note.backend.controller;

import com.note.backend.service.KinopoiskService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kinopoisk")
public class KinopoiskController {
    private final KinopoiskService kinopoiskService;

    public KinopoiskController(KinopoiskService kinopoiskService) {
        this.kinopoiskService = kinopoiskService;
    }

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> popular(@RequestParam(defaultValue = "1") int page) {
        return kinopoiskService.getPopular(page);
    }

    @GetMapping(value = "/movie/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> movie(@PathVariable long id) {
        return kinopoiskService.getMovieDetails(id);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return kinopoiskService.search(query, page);
    }
}
