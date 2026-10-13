package org.example.service;

import org.example.domain.Book;

import java.util.List;

public interface BookService {

    Book insert(String title, long authorId, long genreId);

    Book findById(long id);

    List<Book> findAll();

    Book update(long id, String title, long authorId, long genreId);

    void deleteById(long id);
}
