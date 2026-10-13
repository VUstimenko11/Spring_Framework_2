package org.example.dao;

import org.example.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(GenreDaoJdbc.class)
@DisplayName("GenreDaoJdbc")
class GenreDaoJdbcTest {

    @Autowired
    private GenreDao genreDao;

    @Test
    @DisplayName("should insert genre and return generated id")
    void shouldInsertGenre() {
        Genre inserted = genreDao.insert(new Genre(null, "Poetry"));

        assertThat(inserted.getId()).isNotNull();
        assertThat(genreDao.findById(inserted.getId()))
                .isPresent()
                .get()
                .extracting(Genre::getName)
                .isEqualTo("Poetry");
    }

    @Test
    @DisplayName("should find all seeded genres")
    void shouldFindAllGenres() {
        List<Genre> genres = genreDao.findAll();

        assertThat(genres).hasSizeGreaterThanOrEqualTo(3);
        assertThat(genres).extracting(Genre::getName)
                .contains("Novel", "Drama", "Short story");
    }

    @Test
    @DisplayName("should update genre")
    void shouldUpdateGenre() {
        Genre genre = genreDao.insert(new Genre(null, "Old Genre"));
        genre.setName("New Genre");

        genreDao.update(genre);

        assertThat(genreDao.findById(genre.getId()))
                .isPresent()
                .get()
                .extracting(Genre::getName)
                .isEqualTo("New Genre");
    }

    @Test
    @DisplayName("should delete genre by id")
    void shouldDeleteGenre() {
        Genre genre = genreDao.insert(new Genre(null, "To Delete"));

        genreDao.deleteById(genre.getId());

        assertThat(genreDao.findById(genre.getId())).isEmpty();
    }
}
