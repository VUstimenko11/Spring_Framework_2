package org.example.rest.mapper;

import org.example.domain.Author;
import org.example.rest.dto.AuthorDto;

public final class AuthorMapper {

    private AuthorMapper() {
    }

    public static AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
