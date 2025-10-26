package com.movie.build.controller;

import com.movie.build.model.Movie;
import com.movie.build.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final UserService userService;

    public MovieController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/watched")
    public ResponseEntity<?> addToWatched(@RequestBody MovieRequest req, Authentication auth) {
        String username = auth.getName();
        try {
            userService.addToWatched(username, req.getMovieId(), req.getTitle(), req.getPosterPath(), req.getOverview(), req.getReleaseDate());
            return ResponseEntity.ok("Added to watched");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/watched/{movieId}")
    public ResponseEntity<?> removeFromWatched(@PathVariable Long movieId, Authentication auth) {
        String username = auth.getName();
        try {
            userService.removeFromWatched(username, movieId);
            return ResponseEntity.ok("Removed from watched");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/watched")
    public ResponseEntity<Set<Movie>> getWatched(Authentication auth) {
        String username = auth.getName();
        Set<Movie> movies = userService.getWatchedMovies(username);
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> addToFavorites(@RequestBody MovieRequest req, Authentication auth) {
        String username = auth.getName();
        try {
            userService.addToFavorites(username, req.getMovieId(), req.getTitle(), req.getPosterPath(), req.getOverview(), req.getReleaseDate());
            return ResponseEntity.ok("Added to favorites");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/favorites/{movieId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long movieId, Authentication auth) {
        String username = auth.getName();
        try {
            userService.removeFromFavorites(username, movieId);
            return ResponseEntity.ok("Removed from favorites");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<Set<Movie>> getFavorites(Authentication auth) {
        String username = auth.getName();
        Set<Movie> movies = userService.getFavoriteMovies(username);
        return ResponseEntity.ok(movies);
    }

    public static class MovieRequest {
        private Long movieId;
        private String title;
        private String posterPath;
        private String overview;
        private String releaseDate;

        // Getters and Setters
        public Long getMovieId() { return movieId; }
        public void setMovieId(Long movieId) { this.movieId = movieId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getPosterPath() { return posterPath; }
        public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
        public String getOverview() { return overview; }
        public void setOverview(String overview) { this.overview = overview; }
        public String getReleaseDate() { return releaseDate; }
        public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    }
}