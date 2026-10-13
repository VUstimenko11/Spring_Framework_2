package org.example.dao;

import org.example.domain.Genre;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class GenreDaoJdbc implements GenreDao {

    private static final RowMapper<Genre> GENRE_ROW_MAPPER = (rs, rowNum) ->
            new Genre(rs.getLong("id"), rs.getString("name"));

    private final NamedParameterJdbcOperations jdbc;

    public GenreDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Genre insert(Genre genre) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource("name", genre.getName());
        jdbc.update(
                "INSERT INTO genres (name) VALUES (:name)",
                params,
                keyHolder,
                new String[]{"id"}
        );
        genre.setId(requireGeneratedId(keyHolder));
        return genre;
    }

    @Override
    public Optional<Genre> findById(long id) {
        List<Genre> genres = jdbc.query(
                "SELECT id, name FROM genres WHERE id = :id",
                Map.of("id", id),
                GENRE_ROW_MAPPER
        );
        return genres.stream().findFirst();
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT id, name FROM genres ORDER BY id", GENRE_ROW_MAPPER);
    }

    @Override
    public void update(Genre genre) {
        jdbc.update(
                "UPDATE genres SET name = :name WHERE id = :id",
                new MapSqlParameterSource()
                        .addValue("id", genre.getId())
                        .addValue("name", genre.getName())
        );
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("DELETE FROM genres WHERE id = :id", Map.of("id", id));
    }

    private static long requireGeneratedId(GeneratedKeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated genre id");
        }
        return key.longValue();
    }
}
