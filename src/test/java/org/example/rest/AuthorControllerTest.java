package org.example.rest;

import org.example.domain.Author;
import org.example.service.AuthorService;
import org.example.service.EntityNotFoundException;
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

@WebMvcTest(AuthorController.class)
@Import({AuthorControllerTest.StubConfig.class, RestExceptionHandler.class})
@DisplayName("AuthorController")
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StubAuthorService authorService;

    @BeforeEach
    void setUp() {
        authorService.clear();
    }

    @Test
    @DisplayName("should return all authors")
    void shouldFindAll() throws Exception {
        authorService.put(new Author(1L, "Leo Tolstoy"));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName").value("Leo Tolstoy"));
    }

    @Test
    @DisplayName("should return author by id")
    void shouldFindById() throws Exception {
        authorService.put(new Author(1L, "Leo Tolstoy"));

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Leo Tolstoy"));
    }

    @Test
    @DisplayName("should return 404 when author not found")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/authors/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author not found: id=99"));
    }

    @Test
    @DisplayName("should create author")
    void shouldCreate() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Pushkin\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/authors/")))
                .andExpect(jsonPath("$.fullName").value("Pushkin"));
    }

    @Test
    @DisplayName("should return 400 on blank fullName")
    void shouldRejectBlankFullName() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update author")
    void shouldUpdate() throws Exception {
        authorService.put(new Author(1L, "Old"));

        mockMvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated"));
    }

    @Test
    @DisplayName("should delete author")
    void shouldDelete() throws Exception {
        authorService.put(new Author(1L, "To Delete"));

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class StubConfig {
        @Bean
        StubAuthorService authorService() {
            return new StubAuthorService();
        }
    }

    static final class StubAuthorService implements AuthorService {
        private final Map<Long, Author> authors = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(10);

        void clear() {
            authors.clear();
        }

        void put(Author author) {
            authors.put(author.getId(), author);
        }

        @Override
        public Author insert(String fullName) {
            Author author = new Author(idGenerator.getAndIncrement(), fullName);
            authors.put(author.getId(), author);
            return author;
        }

        @Override
        public Author findById(long id) {
            Author author = authors.get(id);
            if (author == null) {
                throw new EntityNotFoundException("Author not found: id=" + id);
            }
            return author;
        }

        @Override
        public List<Author> findAll() {
            return new ArrayList<>(authors.values());
        }

        @Override
        public Author update(long id, String fullName) {
            Author author = findById(id);
            author.setFullName(fullName);
            return author;
        }

        @Override
        public void deleteById(long id) {
            findById(id);
            authors.remove(id);
        }
    }
}
