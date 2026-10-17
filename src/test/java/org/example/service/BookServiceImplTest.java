package org.example.service;

import org.example.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({BookServiceImpl.class, AuthorServiceImpl.class, GenreServiceImpl.class})
@DisplayName("BookServiceImpl")
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Test
    @DisplayName("should insert book when author and genre exist")
    void shouldInsertBook() {
        Book created = bookService.insert("Resurrection", 1L, 1L);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Resurrection");
        assertThat(created.getAuthor().getFullName()).isEqualTo("Leo Tolstoy");
        assertThat(created.getGenre().getName()).isEqualTo("Novel");
    }

    @Test
    @DisplayName("should find book by id")
    void shouldFindById() {
        Book created = bookService.insert("Hadji Murad", 1L, 1L);

        assertThat(bookService.findById(created.getId()).getTitle()).isEqualTo("Hadji Murad");
    }

    @Test
    @DisplayName("should throw when book not found")
    void shouldThrowWhenBookNotFound() {
        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("should return all books including newly inserted")
    void shouldFindAll() {
        int before = bookService.findAll().size();
        bookService.insert("Book Extra", 1L, 1L);

        assertThat(bookService.findAll()).hasSize(before + 1);
    }

    @Test
    @DisplayName("should update existing book")
    void shouldUpdateBook() {
        Book created = bookService.insert("Old", 1L, 2L);

        Book result = bookService.update(created.getId(), "New Title", 2L, 1L);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getAuthor().getId()).isEqualTo(2L);
        assertThat(result.getGenre().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should delete existing book")
    void shouldDeleteBook() {
        Book created = bookService.insert("Title", 1L, 1L);

        bookService.deleteById(created.getId());

        assertThatThrownBy(() -> bookService.findById(created.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
