package com.note.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class EndpointMappingTests {
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
        r.add("app.jwt.secret", () -> "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
        r.add("app.kinopoisk.api-key", () -> "dummy");
        r.add("app.cors.allowed-origins", () -> "*");
    }

    @Autowired
    RequestMappingHandlerMapping handlerMapping;

    @Test
    void allExpectedApiEndpointsAreRegistered() {
        Set<String> patterns = handlerMapping.getHandlerMethods().keySet().stream()
                .flatMap(info -> {
                    try {
                        RequestMappingInfo rmi = (RequestMappingInfo) info;
                        if (rmi.getPathPatternsCondition() != null) {
                            return rmi.getPathPatternsCondition().getPatterns().stream().map(p -> p.getPatternString());
                        } else if (rmi.getPatternsCondition() != null) {
                            return rmi.getPatternsCondition().getPatterns().stream();
                        } else {
                            return java.util.stream.Stream.of(rmi.toString());
                        }
                    } catch (Throwable t) {
                        return java.util.stream.Stream.of(info.toString());
                    }
                })
                .collect(Collectors.toSet());

        // Check representative endpoints exist
        assertThat(patterns).anyMatch(p -> p.contains("/api/auth/login"));
        assertThat(patterns).anyMatch(p -> p.contains("/api/auth/register"));
        assertThat(patterns).anyMatch(p -> p.contains("/api/auth/me"));

        assertThat(patterns).anyMatch(p -> p.contains("/api/kinopoisk/popular"));
        assertThat(patterns).anyMatch(p -> p.contains("/api/kinopoisk/search"));
        assertThat(patterns).anyMatch(p -> p.contains("/api/kinopoisk/movie/"));

        assertThat(patterns).anyMatch(p -> p.contains("/api/movies/watched"));
        assertThat(patterns).anyMatch(p -> p.contains("/api/movies/favorites"));

        assertThat(patterns).anyMatch(p -> p.contains("/api/users/"));
        assertThat(patterns).anyMatch(p -> p.contains("/api/users/") && p.contains("/watched"));
        assertThat(patterns).anyMatch(p -> p.contains("/api/users/") && p.contains("/favorites"));
    }
}
