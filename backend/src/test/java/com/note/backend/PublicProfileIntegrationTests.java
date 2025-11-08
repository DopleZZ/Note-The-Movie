package com.note.backend;

import com.note.backend.model.FavoriteMovie;
import com.note.backend.model.User;
import com.note.backend.model.WatchedMovie;
import com.note.backend.repository.FavoriteMovieRepository;
import com.note.backend.repository.UserRepository;
import com.note.backend.repository.WatchedMovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PublicProfileIntegrationTests {
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");
    static { postgres.start(); }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("app.jwt.secret", () -> "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
                registry.add("app.kinopoisk.api-key", () -> "dummy");
                registry.add("app.cors.allowed-origins", () -> "*");
    }

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired WatchedMovieRepository watchedRepo;
    @Autowired FavoriteMovieRepository favoriteRepo;

    @Test
    void publicProfileEndpointsReturnData() throws Exception {
        // Arrange: create user and some movies
        User u = new User();
        u.setUsername("public_user");
        u.setPasswordHash("pw");
        u = userRepository.save(u);

        WatchedMovie w = new WatchedMovie();
        w.setUser(u); w.setMovieId(101L); w.setTitle("Watched T");
        watchedRepo.save(w);

        FavoriteMovie f = new FavoriteMovie();
        f.setUser(u); f.setMovieId(202L); f.setTitle("Fav T");
        favoriteRepo.save(f);

        // Act + Assert: public profile
        mockMvc.perform(get("/api/users/" + u.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(u.getId()))
                .andExpect(jsonPath("$.username").value("public_user"));

        mockMvc.perform(get("/api/users/" + u.getId() + "/watched").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101));

        mockMvc.perform(get("/api/users/" + u.getId() + "/favorites").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(202));
    }
}
