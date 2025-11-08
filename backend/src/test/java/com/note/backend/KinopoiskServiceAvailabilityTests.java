package com.note.backend;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class KinopoiskServiceAvailabilityTests {
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");
    static { postgres.start(); }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Use real key from env if present, else dummy (skip tests)
        String apiKey = System.getenv("KINOPOISK_API_KEY");
        if (apiKey == null || apiKey.isBlank()) apiKey = "dummy"; // will cause external call to fail
        String finalApiKey = apiKey;
        r.add("app.kinopoisk.api-key", () -> finalApiKey);
        r.add("app.jwt.secret", () -> "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
        r.add("app.cors.allowed-origins", () -> "*");
    }

    @Autowired MockMvc mockMvc;

    @Test
    void popularEndpointReturnsResultsOrIsSkipped() throws Exception {
        String envKey = System.getenv("KINOPOISK_API_KEY");
        if (envKey == null || envKey.isBlank()) {
            Assumptions.assumeTrue(false, "No real Kinopoisk API key provided; skipping availability test");
        }
        int statusCode = mockMvc.perform(get("/api/kinopoisk/popular?page=1"))
                .andReturn().getResponse().getStatus();
        // Accept 200..299 as success; skip if unauthorized (401)
        if (statusCode == 401) {
            Assumptions.assumeTrue(false, "Unauthorized with provided key; skipping");
        }
        org.assertj.core.api.Assertions.assertThat(statusCode).isBetween(200, 299);
    }

    @Test
    void searchEndpointReturnsResultsOrSkipped() throws Exception {
        String envKey = System.getenv("KINOPOISK_API_KEY");
        if (envKey == null || envKey.isBlank()) {
            Assumptions.assumeTrue(false, "No real Kinopoisk API key provided; skipping availability test");
        }
        int statusCode = mockMvc.perform(get("/api/kinopoisk/search?query=matrix&page=1"))
                .andReturn().getResponse().getStatus();
        if (statusCode == 401) {
            Assumptions.assumeTrue(false, "Unauthorized with provided key; skipping");
        }
        org.assertj.core.api.Assertions.assertThat(statusCode).isBetween(200, 299);
    }
}
