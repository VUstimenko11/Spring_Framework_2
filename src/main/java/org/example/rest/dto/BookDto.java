package org.example.rest.dto;

public record BookDto(Long id, String title, AuthorDto author, GenreDto genre) {
}
