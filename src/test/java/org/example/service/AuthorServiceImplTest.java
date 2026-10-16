package org.example.service;

import org.example.domain.Author;
import org.example.repository.AuthorRepository;
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

@DisplayName("AuthorServiceImpl")
class AuthorServiceImplTest {

    private InMemoryAuthorRepository authorRepository;
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        authorRepository = new InMemoryAuthorRepository();
        authorService = new AuthorServiceImpl(authorRepository);
    }

    @Test
    @DisplayName("should insert author")
    void shouldInsert() {
        Author created = authorService.insert("Pushkin");

        assertThat(created.getId()).isNotNull();
        assertThat(created.getFullName()).isEqualTo("Pushkin");
    }

    @Test
    @DisplayName("should find author by id")
    void shouldFindById() {
        Author created = authorService.insert("Tolstoy");

        assertThat(authorService.findById(created.getId()).getFullName()).isEqualTo("Tolstoy");
    }

    @Test
    @DisplayName("should throw when author not found")
    void shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> authorService.findById(42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    @DisplayName("should find all authors")
    void shouldFindAll() {
        authorService.insert("A");
        authorService.insert("B");

        assertThat(authorService.findAll()).hasSize(2);
    }

    private static final class InMemoryAuthorRepository implements AuthorRepository {
        private final AtomicLong idGenerator = new AtomicLong(1);
        private final Map<Long, Author> authors = new ConcurrentHashMap<>();

        @Override
        public Author insert(Author author) {
            long id = idGenerator.getAndIncrement();
            Author stored = new Author(id, author.getFullName());
            authors.put(id, stored);
            author.setId(id);
            return stored;
        }

        @Override
        public Optional<Author> findById(long id) {
            return Optional.ofNullable(authors.get(id));
        }

        @Override
        public List<Author> findAll() {
            return new ArrayList<>(authors.values());
        }

        @Override
        public void update(Author author) {
            authors.put(author.getId(), new Author(author.getId(), author.getFullName()));
        }

        @Override
        public void deleteById(long id) {
            authors.remove(id);
        }
    }
}
