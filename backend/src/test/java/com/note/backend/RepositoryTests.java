package com.note.backend;

import com.note.backend.model.FavoriteMovie;
import com.note.backend.model.User;
import com.note.backend.repository.FavoriteMovieRepository;
import com.note.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
public class RepositoryTests {

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
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteMovieRepository favoriteMovieRepository;

    @Test
    public void saveUserAndFavorite() {
        User u = new User();
        u.setUsername("repoUser");
        u.setPasswordHash("x");
        u = userRepository.save(u);

        FavoriteMovie fm = new FavoriteMovie();
        fm.setUser(u);
        fm.setMovieId(1L);
        fm.setTitle("T");
        fm = favoriteMovieRepository.save(fm);

        assertThat(favoriteMovieRepository.findByUser(u)).hasSize(1);
        assertThat(favoriteMovieRepository.findByUserAndMovieId(u, 1L)).isPresent();
    }
}
