package org.example.rest;

import org.example.domain.Genre;
import org.example.service.EntityNotFoundException;
import org.example.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@Import({GenreControllerTest.StubConfig.class, RestExceptionHandler.class})
@DisplayName("GenreController")
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StubGenreService genreService;

    @BeforeEach
    void setUp() {
        genreService.clear();
    }

    @Test
    @DisplayName("should return all genres")
    void shouldFindAll() throws Exception {
        genreService.put(new Genre(1L, "Novel"));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Novel"));
    }

    @Test
    @DisplayName("should return genre by id")
    void shouldFindById() throws Exception {
        genreService.put(new Genre(1L, "Novel"));

        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Novel"));
    }

    @Test
    @DisplayName("should return 404 when genre not found")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/genres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Genre not found: id=99"));
    }

    @Test
    @DisplayName("should create genre")
    void shouldCreate() throws Exception {
        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Poetry\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/genres/")))
                .andExpect(jsonPath("$.name").value("Poetry"));
    }

    @Test
    @DisplayName("should return 400 on blank name")
    void shouldRejectBlankName() throws Exception {
        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update genre")
    void shouldUpdate() throws Exception {
        genreService.put(new Genre(1L, "Old"));

        mockMvc.perform(put("/api/genres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("should delete genre")
    void shouldDelete() throws Exception {
        genreService.put(new Genre(1L, "To Delete"));

        mockMvc.perform(delete("/api/genres/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class StubConfig {
        @Bean
        StubGenreService genreService() {
            return new StubGenreService();
        }
    }

    static final class StubGenreService implements GenreService {
        private final Map<Long, Genre> genres = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(10);

        void clear() {
            genres.clear();
        }

        void put(Genre genre) {
            genres.put(genre.getId(), genre);
        }

        @Override
        public Genre insert(String name) {
            Genre genre = new Genre(idGenerator.getAndIncrement(), name);
            genres.put(genre.getId(), genre);
            return genre;
        }

        @Override
        public Genre findById(long id) {
            Genre genre = genres.get(id);
            if (genre == null) {
                throw new EntityNotFoundException("Genre not found: id=" + id);
            }
            return genre;
        }

        @Override
        public List<Genre> findAll() {
            return new ArrayList<>(genres.values());
        }

        @Override
        public Genre update(long id, String name) {
            Genre genre = findById(id);
            genre.setName(name);
            return genre;
        }

        @Override
        public void deleteById(long id) {
            findById(id);
            genres.remove(id);
        }
    }
}
