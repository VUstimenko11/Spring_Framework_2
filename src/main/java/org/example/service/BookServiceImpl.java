package org.example.service;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Genre;
import org.example.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService, GenreService genreService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @Override
    @Transactional
    public Book insert(String title, long authorId, long genreId) {
        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);
        return bookRepository.save(new Book(null, title, author, genre));
    }

    @Override
    @Transactional(readOnly = true)
    public Book findById(long id) {
        return bookRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAllWithDetails();
    }

    @Override
    @Transactional
    public Book update(long id, String title, long authorId, long genreId) {
        Book book = findById(id);
        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        bookRepository.save(book);
        return findById(id);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        findById(id);
        bookRepository.deleteById(id);
    }
}
