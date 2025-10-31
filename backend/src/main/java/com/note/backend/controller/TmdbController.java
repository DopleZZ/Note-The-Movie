package com.note.backend.controller;

import com.note.backend.service.TmdbService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {
    private final TmdbService tmdbService;

    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> popular(@RequestParam(defaultValue = "1") int page) {
        return tmdbService.getPopular(page);
    }

    @GetMapping(value = "/movie/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> movie(@PathVariable long id) {
        return tmdbService.getMovieDetails(id);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return tmdbService.search(query, page);
    }
}

