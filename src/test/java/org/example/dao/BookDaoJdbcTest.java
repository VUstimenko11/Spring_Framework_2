package org.example.dao;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Genre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({BookDaoJdbc.class, AuthorDaoJdbc.class, GenreDaoJdbc.class})
@DisplayName("BookDaoJdbc")
class BookDaoJdbcTest {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private GenreDao genreDao;

    @Test
    @DisplayName("should find all books with author and genre joined")
    void shouldFindAllBooksWithRelations() {
        List<Book> books = bookDao.findAll();

        assertThat(books).hasSizeGreaterThanOrEqualTo(5);
        assertThat(books.get(0).getAuthor()).isNotNull();
        assertThat(books.get(0).getGenre()).isNotNull();
        assertThat(books).extracting(Book::getTitle)
                .contains("War and Peace", "Crime and Punishment");
    }

    @Test
    @DisplayName("should find book by id with relations")
    void shouldFindBookById() {
        Book book = bookDao.findById(1L).orElseThrow();

        assertThat(book.getTitle()).isEqualTo("War and Peace");
        assertThat(book.getAuthor().getFullName()).isEqualTo("Leo Tolstoy");
        assertThat(book.getGenre().getName()).isEqualTo("Novel");
    }

    @Test
    @DisplayName("should insert book")
    void shouldInsertBook() {
        Author author = authorDao.findById(1L).orElseThrow();
        Genre genre = genreDao.findById(1L).orElseThrow();

        Book inserted = bookDao.insert(new Book(null, "Resurrection", author, genre));

        assertThat(inserted.getId()).isNotNull();
        assertThat(bookDao.findById(inserted.getId()))
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("Resurrection");
    }

    @Test
    @DisplayName("should update book")
    void shouldUpdateBook() {
        Author author = authorDao.findById(2L).orElseThrow();
        Genre genre = genreDao.findById(2L).orElseThrow();
        Book book = bookDao.insert(new Book(null, "Temp Title", author, genre));

        book.setTitle("Updated Title");
        book.setAuthor(authorDao.findById(1L).orElseThrow());
        book.setGenre(genreDao.findById(1L).orElseThrow());
        bookDao.update(book);

        Book updated = bookDao.findById(book.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getAuthor().getId()).isEqualTo(1L);
        assertThat(updated.getGenre().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should delete book by id")
    void shouldDeleteBook() {
        Author author = authorDao.findById(1L).orElseThrow();
        Genre genre = genreDao.findById(1L).orElseThrow();
        Book book = bookDao.insert(new Book(null, "To Delete", author, genre));

        bookDao.deleteById(book.getId());

        assertThat(bookDao.findById(book.getId())).isEmpty();
    }
}
