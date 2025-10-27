package com.movie.build.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    private Long id; // TMDB movie ID

    @Column(nullable = false)
    private String title;

    private String posterPath;

    private String overview;

    private String releaseDate;

    @JsonIgnore
    @ManyToMany(mappedBy = "watchedMovies")
    private Set<User> watchedByUsers;

    @JsonIgnore
    @ManyToMany(mappedBy = "favoriteMovies")
    private Set<User> favoritedByUsers;

    public Movie() {}

    public Movie(Long id, String title, String posterPath, String overview, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Set<User> getWatchedByUsers() {
        return watchedByUsers;
    }

    public void setWatchedByUsers(Set<User> watchedByUsers) {
        this.watchedByUsers = watchedByUsers;
    }

    public Set<User> getFavoritedByUsers() {
        return favoritedByUsers;
    }

    public void setFavoritedByUsers(Set<User> favoritedByUsers) {
        this.favoritedByUsers = favoritedByUsers;
    }
}