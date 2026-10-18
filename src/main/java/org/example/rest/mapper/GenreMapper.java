package org.example.rest.mapper;

import org.example.domain.Genre;
import org.example.rest.dto.GenreDto;

public final class GenreMapper {

    private GenreMapper() {
    }

    public static GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
