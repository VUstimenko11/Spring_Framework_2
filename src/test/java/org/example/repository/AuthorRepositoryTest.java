package org.example.repository;

import org.example.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("AuthorRepository")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("should save author and return generated id")
    void shouldSaveAuthor() {
        Author saved = authorRepository.save(new Author(null, "Alexander Pushkin"));

        assertThat(saved.getId()).isNotNull();
        assertThat(authorRepository.findById(saved.getId()))
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
        Author author = authorRepository.save(new Author(null, "Old Name"));
        author.setFullName("New Name");

        authorRepository.save(author);

        assertThat(authorRepository.findById(author.getId()))
                .isPresent()
                .get()
                .extracting(Author::getFullName)
                .isEqualTo("New Name");
    }

    @Test
    @DisplayName("should delete author by id")
    void shouldDeleteAuthor() {
        Author author = authorRepository.save(new Author(null, "To Delete"));

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
