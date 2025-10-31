package com.note.backend.repository;

import com.note.backend.model.FavoriteMovie;
import com.note.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, Long> {
    List<FavoriteMovie> findByUser(User user);
    Optional<FavoriteMovie> findByUserAndMovieId(User user, Long movieId);
    void deleteByUserAndMovieId(User user, Long movieId);
}

