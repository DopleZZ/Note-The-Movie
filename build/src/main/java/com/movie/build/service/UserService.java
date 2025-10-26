package com.movie.build.service;

import com.movie.build.model.Movie;
import com.movie.build.model.User;
import com.movie.build.repository.MovieRepository;
import com.movie.build.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, MovieRepository movieRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String rawPassword) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole("ROLE_USER");
        return userRepository.save(u);
    }

    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public void addToWatched(String username, Long movieId, String title, String posterPath, String overview, String releaseDate) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId).orElseGet(() -> {
            Movie newMovie = new Movie(movieId, title, posterPath, overview, releaseDate);
            return movieRepository.save(newMovie);
        });
        user.getWatchedMovies().add(movie);
        userRepository.save(user);
    }

    public void removeFromWatched(String username, Long movieId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        user.getWatchedMovies().remove(movie);
        userRepository.save(user);
    }

    public Set<Movie> getWatchedMovies(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getWatchedMovies();
    }

    public void addToFavorites(String username, Long movieId, String title, String posterPath, String overview, String releaseDate) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId).orElseGet(() -> {
            Movie newMovie = new Movie(movieId, title, posterPath, overview, releaseDate);
            return movieRepository.save(newMovie);
        });
        user.getFavoriteMovies().add(movie);
        userRepository.save(user);
    }

    public void removeFromFavorites(String username, Long movieId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        user.getFavoriteMovies().remove(movie);
        userRepository.save(user);
    }

    public Set<Movie> getFavoriteMovies(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getFavoriteMovies();
    }
}
