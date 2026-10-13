package org.example.service;

import org.example.domain.Genre;

import java.util.List;

public interface GenreService {

    Genre insert(String name);

    Genre findById(long id);

    List<Genre> findAll();
}
