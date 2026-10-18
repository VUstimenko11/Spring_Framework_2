package org.example.shell;

import org.example.domain.Genre;
import org.example.service.EntityNotFoundException;
import org.example.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GenreCommands")
class GenreCommandsTest {

    private GenreCommands genreCommands;

    @BeforeEach
    void setUp() {
        StubGenreService genreService = new StubGenreService();
        genreService.put(new Genre(1L, "Novel"));
        genreCommands = new GenreCommands(genreService);
    }

    @Test
    @DisplayName("should format genres list")
    void shouldFindAll() {
        assertThat(genreCommands.findAll()).contains("Novel");
    }

    @Test
    @DisplayName("should insert genre")
    void shouldInsert() {
        assertThat(genreCommands.insert("Poetry")).startsWith("Created:");
    }

    private static final class StubGenreService implements GenreService {
        private final Map<Long, Genre> genres = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(10);

        void put(Genre genre) {
            genres.put(genre.getId(), genre);
        }

        @Override
        public Genre insert(String name) {
            Genre genre = new Genre(idGenerator.getAndIncrement(), name);
            genres.put(genre.getId(), genre);
            return genre;
        }

        @Override
        public Genre findById(long id) {
            Genre genre = genres.get(id);
            if (genre == null) {
                throw new EntityNotFoundException("Genre not found: id=" + id);
            }
            return genre;
        }

        @Override
        public List<Genre> findAll() {
            return new ArrayList<>(genres.values());
        }

        @Override
        public Genre update(long id, String name) {
            Genre genre = findById(id);
            genre.setName(name);
            return genre;
        }

        @Override
        public void deleteById(long id) {
            findById(id);
            genres.remove(id);
        }
    }
}
