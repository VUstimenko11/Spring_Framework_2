package org.example.service;

import org.example.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(AuthorServiceImpl.class)
@DisplayName("AuthorServiceImpl")
class AuthorServiceImplTest {

    @Autowired
    private AuthorServiceImpl authorService;

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
    @DisplayName("should find all authors including newly inserted")
    void shouldFindAll() {
        int before = authorService.findAll().size();
        authorService.insert("A");
        authorService.insert("B");

        assertThat(authorService.findAll()).hasSize(before + 2);
    }

    @Test
    @DisplayName("should update author")
    void shouldUpdate() {
        Author created = authorService.insert("Old Name");

        Author updated = authorService.update(created.getId(), "New Name");

        assertThat(updated.getFullName()).isEqualTo("New Name");
        assertThat(authorService.findById(created.getId()).getFullName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("should delete author")
    void shouldDelete() {
        Author created = authorService.insert("To Delete");

        authorService.deleteById(created.getId());

        assertThatThrownBy(() -> authorService.findById(created.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
