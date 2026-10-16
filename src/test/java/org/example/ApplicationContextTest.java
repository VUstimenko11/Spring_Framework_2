package org.example;

import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.repository.CommentRepository;
import org.example.repository.GenreRepository;
import org.example.service.AuthorService;
import org.example.service.BookService;
import org.example.service.CommentService;
import org.example.service.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Application context")
class ApplicationContextTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("should load context and initialize schema with seed data")
    void shouldLoadContextAndSeedData() {
        assertThat(authorRepository.findAll()).isNotEmpty();
        assertThat(genreRepository.findAll()).isNotEmpty();
        assertThat(bookRepository.findAll()).isNotEmpty();
        assertThat(commentRepository.findAll()).isNotEmpty();

        assertThat(authorService.findAll()).isNotEmpty();
        assertThat(genreService.findAll()).isNotEmpty();
        assertThat(bookService.findAll()).isNotEmpty();
        assertThat(commentService.findAll()).isNotEmpty();
    }
}
