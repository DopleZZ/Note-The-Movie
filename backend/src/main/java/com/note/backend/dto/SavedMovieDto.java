package com.note.backend.dto;

public class SavedMovieDto {
    private Long id;
    private String title;
    private String posterPath;
    private String overview;
    private String releaseDate;

    public SavedMovieDto(Long id, String title, String posterPath, String overview, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}

