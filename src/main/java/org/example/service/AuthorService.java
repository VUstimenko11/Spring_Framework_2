package org.example.service;

import org.example.domain.Author;

import java.util.List;

public interface AuthorService {

    Author insert(String fullName);

    Author findById(long id);

    List<Author> findAll();

    Author update(long id, String fullName);

    void deleteById(long id);
}
