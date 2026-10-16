package org.example.repository;

import org.example.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AuthorRepositoryJpa.class)
@DisplayName("AuthorRepositoryJpa")
class AuthorRepositoryJpaTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("should insert author and return generated id")
    void shouldInsertAuthor() {
        Author inserted = authorRepository.insert(new Author(null, "Alexander Pushkin"));

        assertThat(inserted.getId()).isNotNull();
        assertThat(authorRepository.findById(inserted.getId()))
                .isPresent()
                .get()
                .extracting(Author::getFullName)
                .isEqualTo("Alexander Pushkin");
    }

    @Test
    @DisplayName("should find all seeded authors")
    void shouldFindAllAuthors() {
        List<Author> authors = authorRepository.findAll();

        assertThat(authors).hasSizeGreaterThanOrEqualTo(3);
        assertThat(authors).extracting(Author::getFullName)
                .contains("Leo Tolstoy", "Fyodor Dostoevsky", "Anton Chekhov");
    }

    @Test
    @DisplayName("should update author")
    void shouldUpdateAuthor() {
        Author author = authorRepository.insert(new Author(null, "Old Name"));
        author.setFullName("New Name");

        authorRepository.update(author);

        assertThat(authorRepository.findById(author.getId()))
                .isPresent()
                .get()
                .extracting(Author::getFullName)
                .isEqualTo("New Name");
    }

    @Test
    @DisplayName("should delete author by id")
    void shouldDeleteAuthor() {
        Author author = authorRepository.insert(new Author(null, "To Delete"));

        authorRepository.deleteById(author.getId());

        assertThat(authorRepository.findById(author.getId())).isEmpty();
    }

    @Test
    @DisplayName("should return empty optional for unknown id")
    void shouldReturnEmptyForUnknownId() {
        Optional<Author> author = authorRepository.findById(-1L);

        assertThat(author).isEmpty();
    }
}
