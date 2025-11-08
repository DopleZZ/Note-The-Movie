package com.note.backend.controller;

import com.note.backend.dto.SavedMovieDto;
import com.note.backend.model.User;
import com.note.backend.repository.FavoriteMovieRepository;
import com.note.backend.repository.WatchedMovieRepository;
import com.note.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class PublicProfileController {
    private final WatchedMovieRepository watchedRepo;
    private final FavoriteMovieRepository favoriteRepo;
    private final UserService userService;

    public PublicProfileController(WatchedMovieRepository watchedRepo, FavoriteMovieRepository favoriteRepo, UserService userService) {
        this.watchedRepo = watchedRepo;
        this.favoriteRepo = favoriteRepo;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public PublicUserDto getUser(@PathVariable Long id) {
        User u = userService.findByIdOrThrow(id);
        return new PublicUserDto(u.getId(), u.getUsername());
    }

    @GetMapping("/{id}/watched")
    public List<SavedMovieDto> getWatched(@PathVariable Long id) {
        User u = userService.findByIdOrThrow(id);
        return watchedRepo.findByUser(u).stream()
                .map(m -> new SavedMovieDto(m.getMovieId(), m.getTitle(), m.getPosterPath(), m.getOverview(), m.getReleaseDate()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/favorites")
    public List<SavedMovieDto> getFavorites(@PathVariable Long id) {
        User u = userService.findByIdOrThrow(id);
        return favoriteRepo.findByUser(u).stream()
                .map(m -> new SavedMovieDto(m.getMovieId(), m.getTitle(), m.getPosterPath(), m.getOverview(), m.getReleaseDate()))
                .collect(Collectors.toList());
    }

    public static class PublicUserDto {
        public Long id;
        public String username;
        public PublicUserDto(Long id, String username) { this.id = id; this.username = username; }
    }
}
