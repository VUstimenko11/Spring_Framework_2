package org.example.repository;

import org.example.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("GenreRepository")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("should save genre and return generated id")
    void shouldSaveGenre() {
        Genre saved = genreRepository.save(new Genre(null, "Fantasy"));

        assertThat(saved.getId()).isNotNull();
        assertThat(genreRepository.findById(saved.getId()))
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
        Genre genre = genreRepository.save(new Genre(null, "Old"));
        genre.setName("New");

        genreRepository.save(genre);

        assertThat(genreRepository.findById(genre.getId()))
                .isPresent()
                .get()
                .extracting(Genre::getName)
                .isEqualTo("New");
    }

    @Test
    @DisplayName("should delete genre by id")
    void shouldDeleteGenre() {
        Genre genre = genreRepository.save(new Genre(null, "To Delete"));

        genreRepository.deleteById(genre.getId());

        assertThat(genreRepository.findById(genre.getId())).isEmpty();
    }
}
