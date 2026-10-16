package org.example.repository;

import org.example.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book insert(Book book);

    Optional<Book> findById(long id);

    List<Book> findAll();

    void update(Book book);

    void deleteById(long id);
}
