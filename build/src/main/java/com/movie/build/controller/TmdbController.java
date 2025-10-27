package com.movie.build.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    @Value("${VITE_TMDB_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/popular")
    public ResponseEntity<String> getPopularMovies(@RequestParam(defaultValue = "1") int page) {
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey + "&language=ru-RU&page=" + page;
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity<String> getMovieDetails(@PathVariable int id) {
        String url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + apiKey + "&language=ru-RU&append_to_response=credits";
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchMovies(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&language=ru-RU&query=" + query + "&page=" + page;
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }
}