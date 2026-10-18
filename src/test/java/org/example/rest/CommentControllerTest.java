package org.example.rest;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Comment;
import org.example.domain.Genre;
import org.example.service.CommentService;
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
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import({CommentControllerTest.StubConfig.class, RestExceptionHandler.class})
@DisplayName("CommentController")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StubCommentService commentService;

    @BeforeEach
    void setUp() {
        commentService.clear();
    }

    @Test
    @DisplayName("should return all comments")
    void shouldFindAll() throws Exception {
        commentService.put(new Comment(1L, "Nice", book(1L)));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("Nice"))
                .andExpect(jsonPath("$[0].bookId").value(1));
    }

    @Test
    @DisplayName("should return comment by id")
    void shouldFindById() throws Exception {
        commentService.put(new Comment(2L, "Great", book(1L)));

        mockMvc.perform(get("/api/comments/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.text").value("Great"));
    }

    @Test
    @DisplayName("should return comments by book id")
    void shouldFindByBookId() throws Exception {
        commentService.put(new Comment(1L, "A", book(1L)));
        commentService.put(new Comment(2L, "B", book(2L)));

        mockMvc.perform(get("/api/books/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("A"));
    }

    @Test
    @DisplayName("should return 404 when comment not found")
    void shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/comments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment not found: id=99"));
    }

    @Test
    @DisplayName("should create comment")
    void shouldCreate() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"New comment","bookId":5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/comments/")))
                .andExpect(jsonPath("$.text").value("New comment"))
                .andExpect(jsonPath("$.bookId").value(5));
    }

    @Test
    @DisplayName("should reject invalid create payload")
    void shouldRejectInvalidCreate() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"","bookId":null}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update comment")
    void shouldUpdate() throws Exception {
        commentService.put(new Comment(1L, "Old", book(1L)));

        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Updated","bookId":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated"))
                .andExpect(jsonPath("$.bookId").value(2));
    }

    @Test
    @DisplayName("should delete comment")
    void shouldDelete() throws Exception {
        commentService.put(new Comment(1L, "To Delete", book(1L)));

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isNotFound());
    }

    private static Book book(long id) {
        return new Book(id, "Title", new Author(1L, "Author"), new Genre(1L, "Genre"));
    }

    @TestConfiguration
    static class StubConfig {
        @Bean
        StubCommentService commentService() {
            return new StubCommentService();
        }
    }

    static final class StubCommentService implements CommentService {
        private final Map<Long, Comment> comments = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(100);

        void clear() {
            comments.clear();
        }

        void put(Comment comment) {
            comments.put(comment.getId(), comment);
        }

        @Override
        public Comment insert(String text, long bookId) {
            Comment comment = new Comment(idGenerator.getAndIncrement(), text, book(bookId));
            comments.put(comment.getId(), comment);
            return comment;
        }

        @Override
        public Comment findById(long id) {
            Comment comment = comments.get(id);
            if (comment == null) {
                throw new EntityNotFoundException("Comment not found: id=" + id);
            }
            return comment;
        }

        @Override
        public List<Comment> findAll() {
            return new ArrayList<>(comments.values());
        }

        @Override
        public List<Comment> findByBookId(long bookId) {
            return comments.values().stream()
                    .filter(comment -> comment.getBook().getId().equals(bookId))
                    .collect(Collectors.toList());
        }

        @Override
        public Comment update(long id, String text, long bookId) {
            findById(id);
            Comment comment = new Comment(id, text, book(bookId));
            comments.put(id, comment);
            return comment;
        }

        @Override
        public void deleteById(long id) {
            findById(id);
            comments.remove(id);
        }
    }
}
