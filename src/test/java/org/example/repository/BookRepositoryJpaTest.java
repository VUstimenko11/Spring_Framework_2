package org.example.repository;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BookRepositoryJpa.class, AuthorRepositoryJpa.class, GenreRepositoryJpa.class})
@DisplayName("BookRepositoryJpa")
class BookRepositoryJpaTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("should find all books with author and genre joined")
    void shouldFindAllBooksWithRelations() {
        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSizeGreaterThanOrEqualTo(5);
        assertThat(books.get(0).getAuthor()).isNotNull();
        assertThat(books.get(0).getGenre()).isNotNull();
        assertThat(books).extracting(Book::getTitle)
                .contains("War and Peace", "Crime and Punishment");
    }

    @Test
    @DisplayName("should find book by id with relations")
    void shouldFindBookById() {
        Book book = bookRepository.findById(1L).orElseThrow();

        assertThat(book.getTitle()).isEqualTo("War and Peace");
        assertThat(book.getAuthor().getFullName()).isEqualTo("Leo Tolstoy");
        assertThat(book.getGenre().getName()).isEqualTo("Novel");
    }

    @Test
    @DisplayName("should insert book")
    void shouldInsertBook() {
        Author author = authorRepository.findById(1L).orElseThrow();
        Genre genre = genreRepository.findById(1L).orElseThrow();

        Book inserted = bookRepository.insert(new Book(null, "Resurrection", author, genre));

        assertThat(inserted.getId()).isNotNull();
        assertThat(bookRepository.findById(inserted.getId()))
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("Resurrection");
    }

    @Test
    @DisplayName("should update book")
    void shouldUpdateBook() {
        Author author = authorRepository.findById(2L).orElseThrow();
        Genre genre = genreRepository.findById(2L).orElseThrow();
        Book book = bookRepository.insert(new Book(null, "Temp Title", author, genre));

        book.setTitle("Updated Title");
        book.setAuthor(authorRepository.findById(1L).orElseThrow());
        book.setGenre(genreRepository.findById(1L).orElseThrow());
        bookRepository.update(book);

        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getAuthor().getId()).isEqualTo(1L);
        assertThat(updated.getGenre().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should delete book by id")
    void shouldDeleteBook() {
        Author author = authorRepository.findById(1L).orElseThrow();
        Genre genre = genreRepository.findById(1L).orElseThrow();
        Book book = bookRepository.insert(new Book(null, "To Delete", author, genre));

        bookRepository.deleteById(book.getId());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }
}
