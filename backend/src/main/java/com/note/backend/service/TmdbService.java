package com.note.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbService {
    private final String apiKey;
    private final RestTemplate http = new RestTemplate();

    public TmdbService(@Value("${app.tmdb.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    public ResponseEntity<String> getPopular(int page) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.themoviedb.org/3/movie/popular")
                .queryParam("api_key", apiKey)
                .queryParam("page", page)
                .build(true)
                .toUriString();
        return http.getForEntity(url, String.class);
    }

    public ResponseEntity<String> getMovieDetails(long id) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.themoviedb.org/3/movie/" + id)
                .queryParam("api_key", apiKey)
                .queryParam("append_to_response", "credits")
                .build(true)
                .toUriString();
        return http.getForEntity(url, String.class);
    }

    public ResponseEntity<String> search(String query, int page) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.themoviedb.org/3/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", query)
                .queryParam("page", page)
                .build(true)
                .toUriString();
        return http.getForEntity(url, String.class);
    }
}

