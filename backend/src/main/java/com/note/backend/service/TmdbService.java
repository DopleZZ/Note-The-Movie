package com.note.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Deprecated compatibility shim. Internally delegates to the provider-named service.
 * This class exists only to avoid breaking older references while the codebase is
 * migrated to the provider-specific `KinopoiskService`.
 */
@Service
public class TmdbService {
    private final KinopoiskService delegate;

    public TmdbService(KinopoiskService delegate) {
        this.delegate = delegate;
    }

    public ResponseEntity<String> getPopular(int page) {
        return delegate.getPopular(page);
    }

    public ResponseEntity<String> getMovieDetails(long id) {
        return delegate.getMovieDetails(id);
    }

    public ResponseEntity<String> search(String query, int page) {
        return delegate.search(query, page);
    }
}

