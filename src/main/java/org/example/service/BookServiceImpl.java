package org.example.service;

import org.example.dao.BookDao;
import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Genre;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AuthorService authorService;
    private final GenreService genreService;

    public BookServiceImpl(BookDao bookDao, AuthorService authorService, GenreService genreService) {
        this.bookDao = bookDao;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @Override
    public Book insert(String title, long authorId, long genreId) {
        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);
        return bookDao.insert(new Book(null, title, author, genre));
    }

    @Override
    public Book findById(long id) {
        return bookDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: id=" + id));
    }

    @Override
    public List<Book> findAll() {
        return bookDao.findAll();
    }

    @Override
    public Book update(long id, String title, long authorId, long genreId) {
        findById(id);
        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);
        Book book = new Book(id, title, author, genre);
        bookDao.update(book);
        return findById(id);
    }

    @Override
    public void deleteById(long id) {
        findById(id);
        bookDao.deleteById(id);
    }
}
