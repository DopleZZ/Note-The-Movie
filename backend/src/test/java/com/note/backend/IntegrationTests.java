package com.note.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class IntegrationTests {
        static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
                        .withDatabaseName("testdb")
                        .withUsername("postgres")
                        .withPassword("password");

        static {
                postgres.start();
        }

        @DynamicPropertySource
        static void registerProps(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
                registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
                // provide JWT secret (base64 32 bytes) and dummy provider key for tests
                registry.add("app.jwt.secret", () -> "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
                registry.add("app.kinopoisk.api-key", () -> "dummy");
        }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static class AuthReq { public String username; public String password; }

    static class MovieReq { public Long movieId; public String title; public String posterPath; public String overview; public String releaseDate; }

    @Test
    public void registerLoginAndFavoritesFlow() throws Exception {
        AuthReq reg = new AuthReq(); reg.username = "inttest"; reg.password = "pass";
        // register
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // login
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // add favorite
        MovieReq m = new MovieReq(); m.movieId = 99999L; m.title = "X"; m.posterPath = "/p.jpg"; m.overview = "o"; m.releaseDate = "2025-01-01";
        mockMvc.perform(post("/api/movies/favorites")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isOk());

        // list favorites
        mockMvc.perform(get("/api/movies/favorites").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(99999));

        // delete favorite
        mockMvc.perform(delete("/api/movies/favorites/99999").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // list empty
        mockMvc.perform(get("/api/movies/favorites").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
