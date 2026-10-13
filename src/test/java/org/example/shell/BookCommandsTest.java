package org.example.shell;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Genre;
import org.example.service.BookService;
import org.example.service.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookCommands")
class BookCommandsTest {

    private StubBookService bookService;
    private BookCommands bookCommands;

    @BeforeEach
    void setUp() {
        bookService = new StubBookService();
        bookCommands = new BookCommands(bookService);
        bookService.put(new Book(1L, "War and Peace", new Author(1L, "Leo Tolstoy"), new Genre(1L, "Novel")));
        bookService.put(new Book(2L, "Anna Karenina", new Author(1L, "Leo Tolstoy"), new Genre(1L, "Novel")));
    }

    @Test
    @DisplayName("should format book list")
    void shouldFindAll() {
        String result = bookCommands.findAll();

        assertThat(result).contains("War and Peace", "Leo Tolstoy", "Novel");
    }

    @Test
    @DisplayName("should format single book")
    void shouldFindById() {
        assertThat(bookCommands.findById(2L)).contains("Anna Karenina");
    }

    @Test
    @DisplayName("should insert book and return created message")
    void shouldInsert() {
        assertThat(bookCommands.insert("Title", 1L, 2L)).startsWith("Created:");
    }

    @Test
    @DisplayName("should update book and return updated message")
    void shouldUpdate() {
        assertThat(bookCommands.update(1L, "New", 1L, 1L)).startsWith("Updated:");
    }

    @Test
    @DisplayName("should delete book")
    void shouldDelete() {
        String result = bookCommands.delete(1L);

        assertThat(result).isEqualTo("Deleted book id=1");
        assertThat(bookService.wasDeleted()).isTrue();
    }

    private static final class StubBookService implements BookService {
        private final Map<Long, Book> books = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(100);
        private final AtomicBoolean deleted = new AtomicBoolean(false);

        void put(Book book) {
            books.put(book.getId(), book);
        }

        boolean wasDeleted() {
            return deleted.get();
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
            Book book = new Book(id, title, new Author(authorId, "A"), new Genre(genreId, "G"));
            books.put(id, book);
            return book;
        }

        @Override
        public void deleteById(long id) {
            books.remove(id);
            deleted.set(true);
        }
    }
}
