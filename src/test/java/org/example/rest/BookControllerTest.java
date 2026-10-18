package org.example.rest;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Genre;
import org.example.service.BookService;
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

@WebMvcTest(BookController.class)
@Import({BookControllerTest.StubConfig.class, RestExceptionHandler.class})
@DisplayName("BookController")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StubBookService bookService;

    @BeforeEach
    void setUp() {
        bookService.clear();
    }

    @Test
    @DisplayName("should return all books")
    void shouldFindAll() throws Exception {
        bookService.put(new Book(1L, "War and Peace", new Author(1L, "Tolstoy"), new Genre(1L, "Novel")));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("War and Peace"))
                .andExpect(jsonPath("$[0].author.fullName").value("Tolstoy"));
    }

    @Test
    @DisplayName("should return book by id")
    void shouldFindById() throws Exception {
        bookService.put(new Book(2L, "Anna Karenina", new Author(1L, "Tolstoy"), new Genre(1L, "Novel")));

        mockMvc.perform(get("/api/books/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Anna Karenina"));
    }

    @Test
    @DisplayName("should return 404 when book not found")
    void shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found: id=99"));
    }

    @Test
    @DisplayName("should create book")
    void shouldCreate() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"New Book","authorId":1,"genreId":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/books/")))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author.id").value(1))
                .andExpect(jsonPath("$.genre.id").value(2));
    }

    @Test
    @DisplayName("should reject invalid create payload")
    void shouldRejectInvalidCreate() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","authorId":null,"genreId":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update book")
    void shouldUpdate() throws Exception {
        bookService.put(new Book(1L, "Old", new Author(1L, "A"), new Genre(1L, "G")));

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated","authorId":2,"genreId":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.author.id").value(2))
                .andExpect(jsonPath("$.genre.id").value(3));
    }

    @Test
    @DisplayName("should delete book")
    void shouldDelete() throws Exception {
        bookService.put(new Book(1L, "To Delete", new Author(1L, "A"), new Genre(1L, "G")));

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class StubConfig {
        @Bean
        StubBookService bookService() {
            return new StubBookService();
        }
    }

    static final class StubBookService implements BookService {
        private final Map<Long, Book> books = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(100);

        void clear() {
            books.clear();
        }

        void put(Book book) {
            books.put(book.getId(), book);
        }

        @Override
        public Book insert(String title, long authorId, long genreId) {
            Book book = new Book(
                    idGenerator.getAndIncrement(),
                    title,
                    new Author(authorId, "Author"),
                    new Genre(genreId, "Genre")
            );
            books.put(book.getId(), book);
            return book;
        }

        @Override
        public Book findById(long id) {
            Book book = books.get(id);
            if (book == null) {
                throw new EntityNotFoundException("Book not found: id=" + id);
            }
            return book;
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(books.values());
        }

        @Override
        public Book update(long id, String title, long authorId, long genreId) {
            findById(id);
            Book book = new Book(id, title, new Author(authorId, "A"), new Genre(genreId, "G"));
            books.put(id, book);
            return book;
        }

        @Override
        public void deleteById(long id) {
            findById(id);
            books.remove(id);
        }
    }
}
