package org.example.repository;

import org.example.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(GenreRepositoryJpa.class)
@DisplayName("GenreRepositoryJpa")
class GenreRepositoryJpaTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("should insert genre and return generated id")
    void shouldInsertGenre() {
        Genre inserted = genreRepository.insert(new Genre(null, "Fantasy"));

        assertThat(inserted.getId()).isNotNull();
        assertThat(genreRepository.findById(inserted.getId()))
                .isPresent()
                .get()
                .extracting(Genre::getName)
                .isEqualTo("Fantasy");
    }

    @Test
    @DisplayName("should find all seeded genres")
    void shouldFindAllGenres() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres).hasSizeGreaterThanOrEqualTo(3);
        assertThat(genres).extracting(Genre::getName)
                .contains("Novel", "Drama", "Short story");
    }

    @Test
    @DisplayName("should update genre")
    void shouldUpdateGenre() {
        Genre genre = genreRepository.insert(new Genre(null, "Old"));
        genre.setName("New");

        genreRepository.update(genre);

        assertThat(genreRepository.findById(genre.getId()))
                .isPresent()
                .get()
                .extracting(Genre::getName)
                .isEqualTo("New");
    }

    @Test
    @DisplayName("should delete genre by id")
    void shouldDeleteGenre() {
        Genre genre = genreRepository.insert(new Genre(null, "To Delete"));

        genreRepository.deleteById(genre.getId());

        assertThat(genreRepository.findById(genre.getId())).isEmpty();
    }

    @Test
    @DisplayName("should return empty optional for unknown id")
    void shouldReturnEmptyForUnknownId() {
        Optional<Genre> genre = genreRepository.findById(-1L);

        assertThat(genre).isEmpty();
    }
}
