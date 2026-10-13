package org.example.service;

import org.example.dao.GenreDao;
import org.example.domain.Genre;
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

@DisplayName("GenreServiceImpl")
class GenreServiceImplTest {

    private InMemoryGenreDao genreDao;
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        genreDao = new InMemoryGenreDao();
        genreService = new GenreServiceImpl(genreDao);
    }

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
        Genre created = genreService.insert("Novel");

        assertThat(genreService.findById(created.getId()).getName()).isEqualTo("Novel");
    }

    @Test
    @DisplayName("should throw when genre not found")
    void shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> genreService.findById(42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    @DisplayName("should find all genres")
    void shouldFindAll() {
        genreService.insert("A");
        genreService.insert("B");

        assertThat(genreService.findAll()).hasSize(2);
    }

    private static final class InMemoryGenreDao implements GenreDao {
        private final AtomicLong idGenerator = new AtomicLong(1);
        private final Map<Long, Genre> genres = new ConcurrentHashMap<>();

        @Override
        public Genre insert(Genre genre) {
            long id = idGenerator.getAndIncrement();
            Genre stored = new Genre(id, genre.getName());
            genres.put(id, stored);
            genre.setId(id);
            return stored;
        }

        @Override
        public Optional<Genre> findById(long id) {
            return Optional.ofNullable(genres.get(id));
        }

        @Override
        public List<Genre> findAll() {
            return new ArrayList<>(genres.values());
        }

        @Override
        public void update(Genre genre) {
            genres.put(genre.getId(), new Genre(genre.getId(), genre.getName()));
        }

        @Override
        public void deleteById(long id) {
            genres.remove(id);
        }
    }
}
