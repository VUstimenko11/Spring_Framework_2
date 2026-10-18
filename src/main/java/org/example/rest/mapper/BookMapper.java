package org.example.rest.mapper;

import org.example.domain.Book;
import org.example.rest.dto.BookDto;

public final class BookMapper {

    private BookMapper() {
    }

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                AuthorMapper.toDto(book.getAuthor()),
                GenreMapper.toDto(book.getGenre())
        );
    }
}
