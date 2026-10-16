package org.example.service;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Genre;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BookServiceImpl")
class BookServiceImplTest {

    private InMemoryBookRepository bookRepository;
    private StubAuthorService authorService;
    private StubGenreService genreService;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        authorService = new StubAuthorService();
        genreService = new StubGenreService();
        bookService = new BookServiceImpl(bookRepository, authorService, genreService);

        authorService.put(new Author(1L, "Leo Tolstoy"), new Author(2L, "Dostoevsky"));
        genreService.put(new Genre(1L, "Novel"), new Genre(2L, "Drama"));
    }

    @Test
    @DisplayName("should insert book when author and genre exist")
    void shouldInsertBook() {
        Book created = bookService.insert("War and Peace", 1L, 1L);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("War and Peace");
        assertThat(created.getAuthor().getFullName()).isEqualTo("Leo Tolstoy");
        assertThat(created.getGenre().getName()).isEqualTo("Novel");
    }

    @Test
    @DisplayName("should find book by id")
    void shouldFindById() {
        Book created = bookService.insert("Anna Karenina", 1L, 1L);

        assertThat(bookService.findById(created.getId()).getTitle()).isEqualTo("Anna Karenina");
    }

    @Test
    @DisplayName("should throw when book not found")
    void shouldThrowWhenBookNotFound() {
        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("should return all books")
    void shouldFindAll() {
        bookService.insert("Book 1", 1L, 1L);

        assertThat(bookService.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("should update existing book")
    void shouldUpdateBook() {
        Book created = bookService.insert("Old", 1L, 2L);

        Book result = bookService.update(created.getId(), "New Title", 2L, 1L);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getAuthor().getId()).isEqualTo(2L);
        assertThat(result.getGenre().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should delete existing book")
    void shouldDeleteBook() {
        Book created = bookService.insert("Title", 1L, 1L);

        bookService.deleteById(created.getId());

        assertThatThrownBy(() -> bookService.findById(created.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private static final class StubAuthorService implements AuthorService {
        private final Map<Long, Author> authors = new ConcurrentHashMap<>();

        void put(Author... values) {
            for (Author author : values) {
                authors.put(author.getId(), author);
            }
        }

        @Override
        public Author insert(String fullName) {
            throw new UnsupportedOperationException();
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
            return List.copyOf(authors.values());
        }
    }

    private static final class StubGenreService implements GenreService {
        private final Map<Long, Genre> genres = new ConcurrentHashMap<>();

        void put(Genre... values) {
            for (Genre genre : values) {
                genres.put(genre.getId(), genre);
            }
        }

        @Override
        public Genre insert(String name) {
            throw new UnsupportedOperationException();
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
            return List.copyOf(genres.values());
        }
    }

    private static final class InMemoryBookRepository implements BookRepository {
        private final AtomicLong idGenerator = new AtomicLong(1);
        private final Map<Long, Book> books = new ConcurrentHashMap<>();

        @Override
        public Book insert(Book book) {
            long id = idGenerator.getAndIncrement();
            Book stored = new Book(id, book.getTitle(), book.getAuthor(), book.getGenre());
            books.put(id, stored);
            book.setId(id);
            return stored;
        }

        @Override
        public Optional<Book> findById(long id) {
            return Optional.ofNullable(books.get(id));
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(books.values());
        }

        @Override
        public void update(Book book) {
            books.put(book.getId(), new Book(book.getId(), book.getTitle(), book.getAuthor(), book.getGenre()));
        }

        @Override
        public void deleteById(long id) {
            books.remove(id);
        }
    }
}
