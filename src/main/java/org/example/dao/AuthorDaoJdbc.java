package org.example.dao;

import org.example.domain.Author;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AuthorDaoJdbc implements AuthorDao {

    private static final RowMapper<Author> AUTHOR_ROW_MAPPER = (rs, rowNum) ->
            new Author(rs.getLong("id"), rs.getString("full_name"));

    private final NamedParameterJdbcOperations jdbc;

    public AuthorDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Author insert(Author author) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource("fullName", author.getFullName());
        jdbc.update(
                "INSERT INTO authors (full_name) VALUES (:fullName)",
                params,
                keyHolder,
                new String[]{"id"}
        );
        author.setId(requireGeneratedId(keyHolder));
        return author;
    }

    @Override
    public Optional<Author> findById(long id) {
        List<Author> authors = jdbc.query(
                "SELECT id, full_name FROM authors WHERE id = :id",
                Map.of("id", id),
                AUTHOR_ROW_MAPPER
        );
        return authors.stream().findFirst();
    }

    @Override
    public List<Author> findAll() {
        return jdbc.query("SELECT id, full_name FROM authors ORDER BY id", AUTHOR_ROW_MAPPER);
    }

    @Override
    public void update(Author author) {
        jdbc.update(
                "UPDATE authors SET full_name = :fullName WHERE id = :id",
                new MapSqlParameterSource()
                        .addValue("id", author.getId())
                        .addValue("fullName", author.getFullName())
        );
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("DELETE FROM authors WHERE id = :id", Map.of("id", id));
    }

    private static long requireGeneratedId(GeneratedKeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated author id");
        }
        return key.longValue();
    }
}
