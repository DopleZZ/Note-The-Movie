package com.note.backend.repository;

import com.note.backend.model.User;
import com.note.backend.model.WatchedMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchedMovieRepository extends JpaRepository<WatchedMovie, Long> {
    List<WatchedMovie> findByUser(User user);
    Optional<WatchedMovie> findByUserAndMovieId(User user, Long movieId);
    void deleteByUserAndMovieId(User user, Long movieId);
}

