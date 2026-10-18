package org.example.shell;

import org.example.domain.Author;
import org.example.service.AuthorService;
import org.example.service.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthorCommands")
class AuthorCommandsTest {

    private AuthorCommands authorCommands;

    @BeforeEach
    void setUp() {
        StubAuthorService authorService = new StubAuthorService();
        authorService.put(new Author(1L, "Leo Tolstoy"));
        authorCommands = new AuthorCommands(authorService);
    }

    @Test
    @DisplayName("should format authors list")
    void shouldFindAll() {
        assertThat(authorCommands.findAll()).contains("Leo Tolstoy");
    }

    @Test
    @DisplayName("should insert author")
    void shouldInsert() {
        assertThat(authorCommands.insert("Pushkin")).startsWith("Created:");
    }

    private static final class StubAuthorService implements AuthorService {
        private final Map<Long, Author> authors = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(10);

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
