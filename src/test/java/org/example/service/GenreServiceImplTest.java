package org.example.service;

import org.example.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GenreServiceImpl.class)
@DisplayName("GenreServiceImpl")
class GenreServiceImplTest {

    @Autowired
    private GenreServiceImpl genreService;

    @Test
    @DisplayName("should insert genre")
    void shouldInsert() {
        Genre created = genreService.insert("Fantasy");

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Fantasy");
    }

    @Test
    @DisplayName("should find genre by id")
    void shouldFindById() {
        Genre created = genreService.insert("Novel-Extra");

        assertThat(genreService.findById(created.getId()).getName()).isEqualTo("Novel-Extra");
    }

    @Test
    @DisplayName("should throw when genre not found")
    void shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> genreService.findById(42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    @DisplayName("should find all genres including newly inserted")
    void shouldFindAll() {
        int before = genreService.findAll().size();
        genreService.insert("A");
        genreService.insert("B");

        assertThat(genreService.findAll()).hasSize(before + 2);
    }
}
