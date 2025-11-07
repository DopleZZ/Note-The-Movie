package com.note.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Service that calls the Kinopoisk Unofficial API.
 * Exposes the same helper methods previously provided by the old TmdbService
 * but is named to reflect the actual provider.
 */
@Service
public class KinopoiskService {
    private final String apiKey;
    private final String baseUrl;
    private final RestTemplate http = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public KinopoiskService(@Value("${app.kinopoisk.api-key:}") String apiKey,
                            @Value("${app.kinopoisk.base-url:https://kinopoiskapiunofficial.tech}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<String> getPopular(int page) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v2.2/films/top")
                .queryParam("type", "TOP_100_POPULAR_FILMS")
                .queryParam("page", page)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = http.exchange(uri, HttpMethod.GET, entity, String.class);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            try {
                JsonNode root = mapper.readTree(resp.getBody());
                // Kinopoisk returns { pagesCount, films: [...] }
                ObjectNode out = mapper.createObjectNode();
                out.put("page", page);
                if (root.has("pagesCount")) out.put("total_pages", root.get("pagesCount").asInt());
                if (root.has("films")) out.set("results", root.get("films"));
                else out.putArray("results");
                return new ResponseEntity<>(mapper.writeValueAsString(out), resp.getStatusCode());
            } catch (Exception ex) {
                // fall back to raw body if mapping fails
                return new ResponseEntity<>(resp.getBody(), resp.getStatusCode());
            }
        }
        return new ResponseEntity<>(resp.getBody(), resp.getStatusCode());
    }

    public ResponseEntity<String> getMovieDetails(long id) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v2.2/films/" + id)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = http.exchange(uri, HttpMethod.GET, entity, String.class);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            try {
                JsonNode root = mapper.readTree(resp.getBody());
                ObjectNode out = mapFilmNode(root);
                if (root.has("actors") && root.get("actors").isArray()) {
                    out.set("actors", root.get("actors"));
                }
                return new ResponseEntity<>(mapper.writeValueAsString(out), resp.getStatusCode());
            } catch (Exception ex) {
                return new ResponseEntity<>(resp.getBody(), resp.getStatusCode());
            }
        }
        return new ResponseEntity<>(resp.getBody(), resp.getStatusCode());
    }

    public ResponseEntity<String> search(String query, int page) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v2.1/films/search-by-keyword")
                .queryParam("keyword", query)
                .queryParam("page", page)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = http.exchange(uri, HttpMethod.GET, entity, String.class);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            try {
                JsonNode root = mapper.readTree(resp.getBody());
                ObjectNode out = mapper.createObjectNode();
                out.put("page", page);
                if (root.has("pagesCount")) out.put("total_pages", root.get("pagesCount").asInt());
                ArrayNode results = mapper.createArrayNode();
                if (root.has("films") && root.get("films").isArray()) {
                    for (JsonNode f : root.get("films")) {
                        results.add(mapFilmNode(f));
                    }
                }
                out.set("results", results);
                return new ResponseEntity<>(mapper.writeValueAsString(out), resp.getStatusCode());
            } catch (Exception ex) {
                return new ResponseEntity<>(resp.getBody(), resp.getStatusCode());
            }
        }
        return new ResponseEntity<>(resp.getBody(), resp.getStatusCode());
    }

    private ObjectNode mapFilmNode(JsonNode f) {
        ObjectNode out = mapper.createObjectNode();
        if (f.has("filmId")) out.put("id", f.get("filmId").asLong());
        else if (f.has("kinopoiskId")) out.put("id", f.get("kinopoiskId").asLong());
        if (f.has("nameRu") && !f.get("nameRu").isNull()) out.put("title", f.get("nameRu").asText());
        else if (f.has("nameEn") && !f.get("nameEn").isNull()) out.put("title", f.get("nameEn").asText());
        else if (f.has("nameOriginal") && !f.get("nameOriginal").isNull()) out.put("title", f.get("nameOriginal").asText());
        if (f.has("posterUrl") && !f.get("posterUrl").isNull()) {
            out.put("poster_path", f.get("posterUrl").asText());
            out.put("posterUrl", f.get("posterUrl").asText());
        }
        if (f.has("posterUrlPreview") && !f.get("posterUrlPreview").isNull()) {
            out.put("poster_preview", f.get("posterUrlPreview").asText());
        }
        if (f.has("year") && !f.get("year").isNull()) out.put("release_date", f.get("year").asText());
        else if (f.has("releaseDate") && !f.get("releaseDate").isNull()) out.put("release_date", f.get("releaseDate").asText());
        if (f.has("description") && !f.get("description").isNull()) out.put("overview", f.get("description").asText());
        else if (f.has("shortDescription") && !f.get("shortDescription").isNull()) out.put("overview", f.get("shortDescription").asText());
        if (f.has("filmLength") && !f.get("filmLength").isNull()) out.put("filmLength", f.get("filmLength").asText());
        if (f.has("runtime") && !f.get("runtime").isNull()) out.put("runtime", f.get("runtime").asText());
        if (f.has("rating") && !f.get("rating").isNull()) {
            if (f.get("rating").isTextual()) out.put("vote_average", f.get("rating").asText());
            else if (f.get("rating").has("kp")) out.put("vote_average", f.get("rating").get("kp").asText());
        }
        ArrayNode genres = mapper.createArrayNode();
        if (f.has("genres") && f.get("genres").isArray()) {
            for (JsonNode g : f.get("genres")) {
                String gn = g.has("genre") ? g.get("genre").asText() : (g.isTextual() ? g.asText() : null);
                if (gn != null) {
                    ObjectNode go = mapper.createObjectNode();
                    go.put("name", gn);
                    genres.add(go);
                }
            }
        }
        out.set("genres", genres);
        return out;
    }
}
