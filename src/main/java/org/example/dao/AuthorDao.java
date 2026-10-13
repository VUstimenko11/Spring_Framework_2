package org.example.dao;

import org.example.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {

    Author insert(Author author);

    Optional<Author> findById(long id);

    List<Author> findAll();

    void update(Author author);

    void deleteById(long id);
}
