package org.example.repository;

import org.example.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    Genre insert(Genre genre);

    Optional<Genre> findById(long id);

    List<Genre> findAll();

    void update(Genre genre);

    void deleteById(long id);
}
