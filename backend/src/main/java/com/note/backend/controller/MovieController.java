package com.note.backend.controller;

import com.note.backend.dto.MovieRequest;
import com.note.backend.dto.SavedMovieDto;
import com.note.backend.model.FavoriteMovie;
import com.note.backend.model.User;
import com.note.backend.model.WatchedMovie;
import com.note.backend.repository.FavoriteMovieRepository;
import com.note.backend.repository.WatchedMovieRepository;
import com.note.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final WatchedMovieRepository watchedRepo;
    private final FavoriteMovieRepository favoriteRepo;
    private final UserService userService;

    public MovieController(WatchedMovieRepository watchedRepo, FavoriteMovieRepository favoriteRepo, UserService userService) {
        this.watchedRepo = watchedRepo;
        this.favoriteRepo = favoriteRepo;
        this.userService = userService;
    }

    @GetMapping("/watched")
    public List<SavedMovieDto> listWatched() {
        User user = currentUser();
        return watchedRepo.findByUser(user).stream()
                .map(m -> new SavedMovieDto(m.getMovieId(), m.getTitle(), m.getPosterPath(), m.getOverview(), m.getReleaseDate()))
                .collect(Collectors.toList());
    }

    @PostMapping("/watched")
    public ResponseEntity<String> addWatched(@Valid @RequestBody MovieRequest req) {
        User user = currentUser();
        watchedRepo.findByUserAndMovieId(user, req.getMovieId()).orElseGet(() -> {
            WatchedMovie m = new WatchedMovie();
            m.setUser(user);
            m.setMovieId(req.getMovieId());
            m.setTitle(req.getTitle());
            m.setPosterPath(req.getPosterPath());
            m.setOverview(req.getOverview());
            m.setReleaseDate(req.getReleaseDate());
            return watchedRepo.save(m);
        });
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/watched/{movieId}")
    @Transactional
    public ResponseEntity<String> removeWatched(@PathVariable Long movieId) {
        User user = currentUser();
        watchedRepo.deleteByUserAndMovieId(user, movieId);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/favorites")
    public List<SavedMovieDto> listFavorites() {
        User user = currentUser();
        return favoriteRepo.findByUser(user).stream()
                .map(m -> new SavedMovieDto(m.getMovieId(), m.getTitle(), m.getPosterPath(), m.getOverview(), m.getReleaseDate()))
                .collect(Collectors.toList());
    }

    @PostMapping("/favorites")
    public ResponseEntity<String> addFavorite(@Valid @RequestBody MovieRequest req) {
        User user = currentUser();
        favoriteRepo.findByUserAndMovieId(user, req.getMovieId()).orElseGet(() -> {
            FavoriteMovie m = new FavoriteMovie();
            m.setUser(user);
            m.setMovieId(req.getMovieId());
            m.setTitle(req.getTitle());
            m.setPosterPath(req.getPosterPath());
            m.setOverview(req.getOverview());
            m.setReleaseDate(req.getReleaseDate());
            return favoriteRepo.save(m);
        });
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/favorites/{movieId}")
    @Transactional
    public ResponseEntity<String> removeFavorite(@PathVariable Long movieId) {
        User user = currentUser();
        favoriteRepo.deleteByUserAndMovieId(user, movieId);
        return ResponseEntity.ok("OK");
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.findByUsernameOrThrow(username);
    }
}

