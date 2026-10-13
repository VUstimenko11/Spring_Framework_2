package org.example.dao;

import org.example.domain.Author;
import org.example.domain.Book;
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
public class BookDaoJdbc implements BookDao {

    private static final String SELECT_BOOK_WITH_RELATIONS = """
            SELECT b.id AS book_id, b.title AS book_title,
                   a.id AS author_id, a.full_name AS author_full_name,
                   g.id AS genre_id, g.name AS genre_name
            FROM books b
            JOIN authors a ON a.id = b.author_id
            JOIN genres g ON g.id = b.genre_id
            """;

    private static final RowMapper<Book> BOOK_ROW_MAPPER = (rs, rowNum) -> {
        Author author = new Author(rs.getLong("author_id"), rs.getString("author_full_name"));
        Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
        return new Book(rs.getLong("book_id"), rs.getString("book_title"), author, genre);
    };

    private final NamedParameterJdbcOperations jdbc;

    public BookDaoJdbc(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Book insert(Book book) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId())
                .addValue("genreId", book.getGenre().getId());
        jdbc.update(
                "INSERT INTO books (title, author_id, genre_id) VALUES (:title, :authorId, :genreId)",
                params,
                keyHolder,
                new String[]{"id"}
        );
        book.setId(requireGeneratedId(keyHolder));
        return book;
    }

    @Override
    public Optional<Book> findById(long id) {
        List<Book> books = jdbc.query(
                SELECT_BOOK_WITH_RELATIONS + " WHERE b.id = :id",
                Map.of("id", id),
                BOOK_ROW_MAPPER
        );
        return books.stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(SELECT_BOOK_WITH_RELATIONS + " ORDER BY b.id", BOOK_ROW_MAPPER);
    }

    @Override
    public void update(Book book) {
        jdbc.update(
                "UPDATE books SET title = :title, author_id = :authorId, genre_id = :genreId WHERE id = :id",
                new MapSqlParameterSource()
                        .addValue("id", book.getId())
                        .addValue("title", book.getTitle())
                        .addValue("authorId", book.getAuthor().getId())
                        .addValue("genreId", book.getGenre().getId())
        );
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("DELETE FROM books WHERE id = :id", Map.of("id", id));
    }

    private static long requireGeneratedId(GeneratedKeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated book id");
        }
        return key.longValue();
    }
}
