package org.example;

import org.example.dao.AuthorDao;
import org.example.dao.BookDao;
import org.example.dao.GenreDao;
import org.example.service.AuthorService;
import org.example.service.BookService;
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
    private BookDao bookDao;

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private GenreDao genreDao;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Test
    @DisplayName("should load context and initialize schema with seed data")
    void shouldLoadContextAndSeedData() {
        assertThat(authorDao.findAll()).isNotEmpty();
        assertThat(genreDao.findAll()).isNotEmpty();
        assertThat(bookDao.findAll()).isNotEmpty();

        assertThat(authorService.findAll()).isNotEmpty();
        assertThat(genreService.findAll()).isNotEmpty();
        assertThat(bookService.findAll()).isNotEmpty();
    }
}
