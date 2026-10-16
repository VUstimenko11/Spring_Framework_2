package org.example.repository;

import org.example.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {

    Author insert(Author author);

    Optional<Author> findById(long id);

    List<Author> findAll();

    void update(Author author);

    void deleteById(long id);
}
